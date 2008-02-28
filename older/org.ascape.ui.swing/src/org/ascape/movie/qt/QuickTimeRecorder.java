/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.movie.qt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JDesktopPane;

import org.ascape.movie.MovieRecorder;

import quicktime.Errors;
import quicktime.QTSession;
import quicktime.app.display.QTCanvas;
import quicktime.app.image.Paintable;
import quicktime.app.image.QTImageDrawer;
import quicktime.app.image.Redrawable;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.image.CSequence;
import quicktime.std.image.CodecComponent;
import quicktime.std.image.CompressedFrameInfo;
import quicktime.std.image.ImageDescription;
import quicktime.std.image.QTImage;
import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.VideoMedia;
import quicktime.util.QTHandle;
import quicktime.util.RawEncodedImage;


/**
 * A class that manages the recording of QuickTime movies.
 *
 * @author Miles Parker
 * @version 2.9
 * @history 5/9/2002 changed to extend Graphics Recorder, moved, other refactorings
 * @history 9/1/2000 first in
 * @since Ascape 1.9
 */
public class QuickTimeRecorder extends MovieRecorder implements StdQTConstants, Errors {

    private Movie movie;

    private QTImageDrawer qid;

    private int kWidth;

    private int kHeight;

    private CSequence seq;

    private QDGraphics gw;

    private QDRect rect;

    private RawEncodedImage compressedImage;

    private VideoMedia vidMedia;

    private QTHandle imageHandle;

    private ImageDescription desc;

    private QTCanvas canv;

    private Track vidTrack;

    public final static int QUALITY_MINIMUM = codecMinQuality;

    public final static int QUALITY_NORMAL = codecNormalQuality;

    public final static int QUALITY_MAXIMUM = codecMaxQuality;

    private int quality = QUALITY_MAXIMUM;

    int curSample = 0;

    interface Quality {

        public int getValue();
    }

    static Quality quality_min = new Quality() {
        public int getValue() {
            return codecMinQuality;
        }

        public String toString() {
            return "Minimum";
        }
    };

    static Quality quality_normal = new Quality() {
        public int getValue() {
            return codecNormalQuality;
        }

        public String toString() {
            return "Normal";
        }
    };

    static Quality quality_max = new Quality() {
        public int getValue() {
            return codecMaxQuality;
        }

        public String toString() {
            return "Maximum";
        }
    };

    private static Quality[] qualities = {quality_min, quality_normal, quality_max};

    /**
     * Constructs the recorder.
     */
    public QuickTimeRecorder() {
        this(null);
    }

    /**
     * Constructs the recorder.
     */
    public QuickTimeRecorder(JDesktopPane desktop) {
        super(desktop);
        try {
            QTSession.open();
        } catch (Exception e) {
            e.printStackTrace();
            QTSession.close();
        }
    }
//
//    /**
//     * Constructs the recorder.
//     *
//     * @param the component to record.
//     */
//    public QuickTimeRecorder(Component component) {
//        this();
//        setComponent(component);
//    }
//
//    /**
//     * Constructs the recorder.
//     *
//     * @param file the file to save recording to
//     * @param the component to record.
//     */
//    public QuickTimeRecorder(File file, Component component) {
//        this();
//        setFile(file);
//        setComponent(component);
//    }
//
//    /**
//     * Constructs the recorder.
//     *
//     * @param file the file to save recording to
//     * @param the target to record.
//     */
//    public QuickTimeRecorder(File file, QuicktimeTarget target) {
//        this();
//        setFile(file);
//        setTarget(target);
//    }

    /**
     * Sets the target to record.
     */
    protected void setTarget(QuicktimeTarget recorderTarget) {
        this.target = recorderTarget;
        if ((listener != null) && (target != null)) {
            requestFile();
        }
    }

    /**
     * Sets a component that this quicktime recorder will record.
     * Conveneince method; will create a component quicktime target and add it.
     * Note that you can have a target or a component being recorder; not both.
     */
    public void setComponent(Component component) {
        setTarget(new ComponentQuicktimeTarget(component));
    }

    /**
     * Creates the list of qualities the recorder supports.
     */
    public JComboBox createQualityComboBox() {
        JComboBox qualityComboBox = new JComboBox(qualities);
        qualityComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                QuickTimeRecorder.this.setQuality(((Quality) e.getItem()).getValue());
            }
        });
        qualityComboBox.setSelectedItem(quality_normal);
        return qualityComboBox;
    }

    /**
     * Creates the movie, preparing it for recording of frames.
     */
    public void start() {
        //No idea why subtracting the 1 from width seems to work, but it does!
        //Without it, the recording is completely garbled or blank, at least on views with equal hight and width. Weird.
        //kWidth = target.getComponent().getSize().width - 1;
        kWidth = target.getComponent().getSize().width;
        kHeight = target.getComponent().getSize().height;
        canv = new QTCanvas(QTCanvas.kInitialSize, 0.5F, 0.5F);
        Frame displayFrame = new Frame();
        displayFrame.add("Center", canv);
        //displayFrame.show();
        displayFrame.pack();
        try {
            qid = new QTImageDrawer((Paintable) target, new Dimension(kWidth, kHeight), Redrawable.kMultiFrame);
            qid.setRedrawing(true);
            canv.setClient(qid, true);

            int kNoVolume = 0;

            vidTrack = movie.addTrack(kWidth, kHeight, kNoVolume);

            vidMedia = new VideoMedia(vidTrack, getFramesPerSecond());

            vidMedia.beginEdits();

            rect = new QDRect(kWidth, kHeight);
            gw = new QDGraphics(rect);
            int size = QTImage.getMaxCompressionSize(gw,
                rect,
                gw.getPixMap().getPixelSize(),
                quality,
                kAnimationCodecType,
                CodecComponent.anyCodec);
            imageHandle = new QTHandle(size, true);
            imageHandle.lock();
            compressedImage = RawEncodedImage.fromQTHandle(imageHandle);
            seq = new CSequence(gw,
                rect,
                gw.getPixMap().getPixelSize(),
                kAnimationCodecType,
                CodecComponent.bestFidelityCodec,
                codecNormalQuality,
                codecNormalQuality,
                0, //1 key viewFrame
                null, //cTab,
                0);
            desc = seq.getDescription();
            qid.redraw(null);
            qid.setGWorld(gw);
            qid.setDisplayBounds(rect);
        } catch (Exception qte) {
            qte.printStackTrace();
        }
        super.start();
    }

    /**
     * Records one frame of the recording at the specified frames per second.
     */
    public void recordFrame() {
        super.recordFrame();
        recordFrameNum++;
        try {
            qid.redraw(null);
            CompressedFrameInfo info = seq.compressFrame(gw,
                rect,
                codecFlagUpdatePrevious,
                compressedImage);
            boolean isKeyFrame = info.getSimilarity() == 0;
            vidMedia.addSample(imageHandle,
                0, // dataOffset,
                info.getDataSize(),
                1, //60, // frameDuration, 60/600 = 1/10 of a second, desired time per viewFrame
                desc,
                1, // one sample
                (isKeyFrame ? 0 : mediaSampleNotSync)); // no flags
        } catch (Exception e) {
            e.printStackTrace();
            QTSession.close();
        }
    }

    /**
     * CLoses the quicktim recorder and the file, and perfroms any other cleanup.
     */
    public void close() {
        if (qid != null) {
            try {
                qid.setGWorld(canv.getPort());
                qid.redraw(null);

                vidMedia.endEdits();
                int kTrackStart = 0;
                int kMediaTime = 0;
                int kMediaRate = 1;
                vidTrack.insertMedia(kTrackStart, kMediaTime, vidMedia.getDuration(), kMediaRate);

                OpenMovieFile outStream = OpenMovieFile.asWrite((QTFile) file);
                movie.addResource(outStream, movieInDataForkResID, file.getName());
                outStream.close();
                QTSession.close();
            } catch (Exception e) {
                e.printStackTrace();
                QTSession.close();
            }
        }
        if (target != null) {
            target = null;
        }
    }

    /**
     * Returns the current quicktime quality level.
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Sets the current quicktime quality level.
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    /**
     * Sets the file to save the movie to.
     */
    public void setFile(File file) {
        try {
            this.file = new QTFile(file);
            movie = Movie.createMovieFile((QTFile) this.file,
                kMoviePlayer,
                createMovieFileDeleteCurFile | createMovieFileDontCreateResFile);
        } catch (Exception qte) {
            qte.printStackTrace();
        }
        super.setFile(file);
    }
}




