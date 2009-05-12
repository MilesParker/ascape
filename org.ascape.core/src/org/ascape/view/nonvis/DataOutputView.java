/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.nonvis;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;

import org.ascape.model.event.ScapeEvent;
import org.ascape.util.PropertyAccessor;
import org.ascape.util.Utility;

/**
 * A non-graphic view providing output of model data to a file. To use,
 * (assuming you are collecting the statistics you are interested in, see Scape)
 * just add this view to any scape, and set a file or data strem for it. Every
 * period, statistic measurements will be written to the file or data strem. By
 * default, all statistics are selected; get data selection to make different
 * selections. If you would like to add more information to the period or run
 * data, simply override the write.. methods. The rule here is that all fields
 * must be preceeded by the '\t' character. (Of course, the first field should
 * not be preceeded with a tab character, but ordinarily you will be calling the
 * super method first anyway.) If you do override either writeRunData or
 * writePeriodData, be sure to override writeRunHeader or writePeriodHeader as
 * well.
 * 
 * @see org.ascape.view.nonvis.DataView
 * @see org.ascape.util.sweep.SweepGroup
 * @see org.ascape.view.nonvis.SweepControlView
 * @see org.ascape.model.Scape
 * @author Miles Parker
 * @version 1.9 8/1/2000
 * @history 1.9 8/1/2000 Complete reimplementation
 * @history 1.0 3/15/99 documentation
 * @since 1.0
 */
public class DataOutputView extends DataView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The number of characters to use for a file name. File names are the
     * sequential number, left padded with '0' to this size. Default is 4. For
     * example, 35 would appear as '0035'. Obviously, a given size allows
     * 10^size possible runs per parameter space. Setting runs per will change
     * this value.
     */
    private static int numCharsInFileName = 4;

    /**
     * The number used for naming each consecutive run. Not 0-based, the first
     * run is run 1.
     */
    protected int runCount = 1;

    /**
     * The character(s) to use for end-of-line.
     */
    public static String EOLString = System.getProperty("line.separator");

    /**
     * The max periods.
     */
    int maxPeriods = Integer.MAX_VALUE;

    /**
     * The write headers.
     */
    protected boolean writeHeaders = true;

    /**
     * The run headers written.
     */
    protected boolean runHeadersWritten = false;

    /**
     * The period headers written.
     */
    protected boolean periodHeadersWritten = false;

    /**
     * The iteration data in sperate files.
     */
    boolean iterationDataInSperateFiles = false;;

    /**
     * The time the current run was started. Used to track elapsed time for each
     * run.
     */
    protected long startTime;

    /**
     * The run file.
     */
    protected File runFile;

    /**
     * The file stream statistics are written to.
     */
    private transient FileOutputStream runFileStream;

    /**
     * The data stream statistics are written to.
     */
    protected transient DataOutputStream runDataStream;

    /**
     * The period file.
     */
    private File periodFile;

    /**
     * The file stream statistics are written to.
     */
    private transient FileOutputStream periodFileStream;

    /**
     * The data stream statistics are written to.
     */
    private transient DataOutputStream periodDataStream;

    /**
     * Constructs a data output view. A stream must be set, and the view must be
     * added to a scape.
     */
    public DataOutputView() {
    }

    /**
     * Constructs a data output view. The view must be added to a scape.
     * 
     * @param runDataStream
     *            the stream to write the data to
     */
    public DataOutputView(DataOutputStream runDataStream) {
        this();
        setRunDataStream(runDataStream);
    }

    /**
     * Constructs a data output view. The view must be added to a scape.
     * 
     * @param file
     *            the file to write the data to
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @exception java.io.IOException
     *                if a file stream cannot be created from the file
     */
    public DataOutputView(File file) throws IOException {
        this();
        setRunFile(file);
    }

    /**
     * Returns the data stream that statistics output should go to.
     * 
     * @return runDataStream the stream to write the data to
     */
    public DataOutputStream getRunDataStream() {
        return runDataStream;
    }

    /**
     * Defines the data stream that run output will be written to.
     * 
     * @param runDataStream
     *            the stream to write the data to
     */
    public void setRunDataStream(DataOutputStream runDataStream) {
        if (this.runDataStream != null) {
            try {
                this.runDataStream.close();
            } catch (IOException e) {
            } //Don't care, the stream might not be open
        }
        this.runDataStream = runDataStream;
        runHeadersWritten = false;
    }

    /**
     * Returns the file that the run output will be written to.
     * 
     * @return runDataStream the stream to write the data to
     */
    public File getRunFile() {
        return runFile;
    }

    /**
     * Defines the file that run output will be written to. Set to null
     * (default) if you don't want run data collected.
     * 
     * @param file
     *            the file to write the data to
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @exception java.io.IOException
     *                if a file stream cannot be created from the file
     */
    public void setRunFile(File file) throws IOException {
        runFile = file;
        file.createNewFile();
        if (this.runFileStream != null) {
            try {
                this.runFileStream.close();
            } catch (IOException e) {
            } //Don't care, the stream might not be open
        }

        if (file != null) {
            runFileStream = new FileOutputStream(file);
            setRunDataStream(new DataOutputStream(runFileStream));
        }
    }

    /**
     * Returns the data stream that period (iteration by iteration) output will
     * be written to.
     * 
     * @return the period data stream
     */
    public DataOutputStream getPeriodDataStream() {
        return periodDataStream;
    }

    /**
     * Defines the data stream that period (iteration by iteration) output will
     * be written to.
     * 
     * @param periodDataStream
     *            the stream to write the data to
     */
    public void setPeriodDataStream(DataOutputStream periodDataStream) {
        if (this.periodDataStream != null) {
            try {
                this.periodDataStream.close();
            } catch (IOException e) {
            } //Don't care, the stream might not be open
        }
        this.periodDataStream = periodDataStream;
        periodHeadersWritten = false;
    }

    /**
     * Gets the period file.
     * 
     * @return the period file
     */
    public File getPeriodFile() {
        return periodFile;
    }

    /**
     * Defines the file that period (iteration by iteeration) output will be
     * written to. Set to null (default) if you don't want period data
     * collected.
     * 
     * @param file
     *            the file to write the data to
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @exception java.io.IOException
     *                if a file stream cannot be created from the file
     */
    public void setPeriodFile(File file) throws IOException {
        periodFile = file;
        file.createNewFile();
        if (this.periodFileStream != null) {
            try {
                this.periodFileStream.close();
            } catch (IOException e) {
            } //Don't care, the stream might not be open
        }

        if (file != null) {
            periodFileStream = new FileOutputStream(file);
            setPeriodDataStream(new DataOutputStream(periodFileStream));
        }
    }

    /**
     * Writes the headers for the period statistics to the run data stream or
     * file. Data is output as tab delimited ASCII text, with the selected
     * "EOLSeparator" separating each period.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writePeriodHeader() throws IOException {
        periodDataStream.writeBytes("Run\t");
        periodDataStream.writeBytes("Period");

        writeStatsHeader(periodDataStream);
    }

    /**
     * Write stats header.
     * 
     * @param stream
     *            the stream
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void writeStatsHeader(DataOutputStream stream) throws IOException {
        for (int i = 0; i < dataSelection.getSelectionSize(); i++) {
            stream.writeBytes("\t");
            stream.writeBytes(dataSelection.getSelectedSeries(i).getName());
        }
    }

    /**
     * Writes the current period statistics to the data stream or file. Data is
     * output as tab delimited ASCII text, with the selected "EOLSeparator"
     * separating each period.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writePeriodData() throws IOException {
        periodDataStream.writeBytes(Integer.toString(runCount));
        periodDataStream.writeBytes("\t");
        periodDataStream.writeBytes(Integer.toString(scape.getPeriod()));

        writeStats(periodDataStream);
    }

    /**
     * Write stats.
     * 
     * @param stream
     *            the stream
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writeStats(DataOutputStream stream) throws IOException {
        for (int i = 0; i < dataSelection.getSelectionSize(); i++) {
            stream.writeBytes("\t");
            stream.writeBytes(Double.toString(dataSelection.getSelectedSeries(i).getValue()));
        }
    }

    /**
     * Writes the headers for the run parameters to the run data stream or file.
     * Data is output as tab delimited ASCII text, with the selected
     * "EOLSeparator" separating each period.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writeRunHeader() throws IOException {
        runDataStream.writeBytes("Run\t");
        runDataStream.writeBytes("Start Period\t");
        runDataStream.writeBytes("Stop Period\t");
        runDataStream.writeBytes("Seed");
        List accessors = scape.retrieveAllAccessorsOrdered();
        for (Iterator iterator = accessors.iterator(); iterator.hasNext();) {
            PropertyAccessor accessor = (PropertyAccessor) iterator.next();
            runDataStream.writeBytes("\t");
            runDataStream.writeBytes(accessor.getLongName());
        }
        writeStatsHeader(runDataStream);
    }

    /**
     * Writes the current run parameters to the data stream or file. Data is
     * output as tab delimited ASCII text, with the selected "EOLSeparator"
     * separating each period.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void writeRunData() throws IOException {
        runDataStream.writeBytes(Integer.toString(runCount));
        runDataStream.writeBytes("\t");
        runDataStream.writeBytes(Integer.toString(scape.getStartPeriod()));
        runDataStream.writeBytes("\t");
        runDataStream.writeBytes(Integer.toString(scape.getPeriod()));
        runDataStream.writeBytes("\t");
        runDataStream.writeBytes(Long.toString(scape.getRandomSeed()));
        List accessors = scape.retrieveAllAccessorsOrdered();
        for (Iterator iterator = accessors.iterator(); iterator.hasNext();) {
            PropertyAccessor accessor = (PropertyAccessor) iterator.next();
            runDataStream.writeBytes("\t");
            if (accessor.getValue() instanceof Number) {
                runDataStream.writeBytes(Utility.formatToString(((Number) accessor.getValue()).doubleValue(), 4));
            } else if (accessor.getValue() instanceof Boolean) {
                if (((Boolean) accessor.getValue()).booleanValue() == false) {
                    runDataStream.writeBytes("False");
                } else {
                    runDataStream.writeBytes("True");
                }
            } else {
                //For the moment, we don't want to print out string setting names..
                //System.out.println(Utility.padStringRight(accessors[i].getLongName() + ":", longestNameLength + 3) + ((String) accessors[i].getValue()));
            }
        }
        writeStats(runDataStream);
    }

    /**
     * (Conveneince method.) If this method returns true, the current run will
     * be stopped.
     * 
     * @return true, if stop condition
     */
    public boolean stopCondition() {
        return false;
    }

    /**
     * Returns the number of characters used for the sequential file name.
     * 
     * @return the num chars in file name
     */
    public static int getNumCharsInFileName() {
        return numCharsInFileName;
    }

    /**
     * Sets the number of characters to use for the sequential file name. File
     * names are left padded with '0' to this number.
     * 
     * @param _numCharsInFileName
     *            the _num chars in file name
     */
    public static void setNumCharsInFileName(int _numCharsInFileName) {
        numCharsInFileName = _numCharsInFileName;
    }

    /**
     * Called for any model change that effects model state; i.e. scapeStarted
     * and scapeIterated.
     */
    protected void scapeChanged() {
        if (periodDataStream != null) {
            try {
                writePeriodData();
                periodDataStream.writeBytes(EOLString);
            } catch (IOException e) {
                throw new RuntimeException("IO Exception occurred while sending iteration data.");
            }
        }
    }

    /**
     * On scape iterate, write period data.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeIterated(ScapeEvent scapeEvent) {
        scapeChanged();
        if (stopCondition()) {
            scape.getRunner().stop();
        }
    }

    /**
     * On close, closes any open files.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeClosing(ScapeEvent scapeEvent) {
        if (runDataStream != null) {
            try {
                runDataStream.close();
            } catch (IOException e) {
            } //Don't care, the stream might not be open
        }
        if (runFileStream != null) {
            try {
                runFileStream.close();
            } catch (IOException e) {
            } //Don't care, the stream might not be open
        }
        if (periodDataStream != null) {
            try {
                periodDataStream.close();
            } catch (IOException e) {
            } //Don't care, the stream might not be open
        }
        if (periodFileStream != null) {
            try {
                periodFileStream.close();
            } catch (IOException e) {
            } //Don't care, the stream might not be open
        }
    }

    /**
     * On scape start, record the start time and wirte the file headers. If any
     * headers need to be written, they are written now. Write first period
     * data.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStarted(ScapeEvent scapeEvent) {
        startTime = System.currentTimeMillis();
        if (writeHeaders) {
            if (!runHeadersWritten && runDataStream != null) {
                try {
                    writeRunHeader();
                    runDataStream.writeBytes(EOLString);
                    runHeadersWritten = true;
                } catch (IOException e) {
                    throw new RuntimeException("IO Exception occurred while sending iteration data: " + e);
                }
            }
            if (!periodHeadersWritten && periodDataStream != null) {
                try {
                    writePeriodHeader();
                    periodDataStream.writeBytes(EOLString);
                    periodHeadersWritten = true;
                } catch (IOException e) {
                    throw new RuntimeException("IO Exception occurred while sending iteration data.");
                }
            }
        }
        //Just a firendly warning message..no point adding this view if you're not going to use it!
        if (runDataStream == null && periodDataStream == null) {
            getScape().getEnvironment().getConsole().println("Warning: DataOutput view added, but no output stream specified!");
        }
        scapeChanged();
    }

    /**
     * On scape stop, writes the run parameters to the file.
     * 
     * @param scapeEvent
     *            the scape event
     * @see org.ascape.view.nonvis.DataOutputView#scapeStarted
     * @see org.ascape.view.nonvis.DataOutputView#scapeStopped
     */
    public void scapeStopped(ScapeEvent scapeEvent) {
        if (runDataStream != null) {
            try {
                writeRunData();
                runDataStream.writeBytes(EOLString);
            } catch (IOException e) {
                throw new RuntimeException("IO Exception occurred while sending run data. " + e);
            }
        }
        runCount++;
    }

    /**
     * Method called once a model is deserialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeDeserialized(ScapeEvent scapeEvent) {
        super.scapeDeserialized(scapeEvent);
        try {
            setPeriodFile(periodFile);
            setRunFile(runFile);
        } catch (IOException e) {
            System.err.println("There was a problem opening a file for data output:\n" + e);
        }
    }

    /**
     * Notifies the listener that the scape has added it. Creates a new data
     * selection for data output, backed by the Scape's data group.
     * 
     * @param scapeEvent
     *            the scape added notification event
     * @throws TooManyListenersException
     *             the too many listeners exception
     * @exception java.util.TooManyListenersException
     *                on attempt to add this listener to another scape when one
     *                has already been assigned
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        dataSelection.selectAll();
        /*try {
            dataPath = scape.getHome() + "ResultData/";//" + Long.toString(System.currentTimeMillis())
            boolean success = (new File(dataPath)).mkdirs();
            if (!success) {
                throw new RuntimeException("Couldn't create directories for data output view.");
            }
		}
		catch (SecurityException e) {
            throw new RuntimeException("Couldn't create directories for data output view: " + e);
		}*/
    }

    /* (non-Javadoc)
     * @see org.ascape.model.event.DefaultScapeListener#toString()
     */
    public String toString() {
        return "Data Output View";
    }

    /**
     * Returns the character(s) to be used to indicate an end-of-line; i.e. the
     * record delimiter. By default the platform dependent property returned by
     * the system property "line.separator".
     * 
     * @return the EOL separator
     */
    public String getEOLSeparator() {
        return EOLString;
    }

    /**
     * Sets the character(s) to be used to indicate an end-of-line; i.e. the
     * record delimiter.
     * 
     * @param string
     *            the string
     */
    public static void setEOLSeparator(String string) {
        EOLString = string;
    }
}
