package cn.songm.im.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

public class FormattingPrintWriter extends PrintWriter {
    private static final String NEWLINE = System.getProperty("line.separator",
            "/r/n");
    // Standard tab settings
    private static final int[] STD_TABS = { 9, 17, 25, 33, 41, 49, 57, 65, 73,
            81 };
    private boolean _autoFlush;
    private int[] _tabs = STD_TABS;
    @SuppressWarnings("unchecked")
    private Stack _stack = new Stack();
    private int _indent;
    private int _pos;
    /**
     * Returns a string consisting of the specified number of spaces.
     * 
     * @return The requested whitespace string.
     */
    private static String spaces(int n) {
        char[] ca = new char[n];
        for (int i = 0; i < n; i++)
            ca[i] = ' ';
        return new String(ca, 0, ca.length);
    }
    /**
     * Constructs a new FormattingPrintWriter, without automatic line flushing.
     * 
     * @param out
     *            A character-output stream.
     */
    public FormattingPrintWriter(Writer out) {
        super(out);
    }
    /**
     * Constructs a new FormattingPrintWriter.
     * 
     * @param out
     *            A character-output stream.
     * @param autoFlush
     *            If <code>true</code>, the println() methods will flush the
     *            output buffer.
     */
    public FormattingPrintWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
        _autoFlush = autoFlush;
    }
    /**
     * Constructs a new PrintWriter, without automatic line flushing, from an
     * existing OutputStream. This convenience constructor creates the necessary
     * intermediate OutputStreamWriter, which will convert characters into bytes
     * using the default character encoding.
     * 
     * @param out
     *            An output stream.
     */
    public FormattingPrintWriter(OutputStream out) {
        super(out);
    }
    /**
     * Constructs a new PrintWriter from an existing OutputStream. This
     * convenience constructor creates the necessary intermediate
     * OutputStreamWriter, which will convert characters into bytes using the
     * default character encoding.
     * 
     * @param out
     *            An output stream.
     * @param autoFlush
     *            if <code>true</code>, the println() methods will flush the
     *            output buffer.
     */
    public FormattingPrintWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
        _autoFlush = autoFlush;
    }
    /**
     * Restores the default tab stops.
     */
    public void clearTabs() {
        setTabs(null);
    }
    /**
     * Sets custom tab stops. At output positions past the rightmost tab stop,
     * tab characters are converted into single spaces.
     * 
     * @param tabs
     *            Unity-based tab stop positions, as an ascending sequence of
     *            positive integers.
     */
    public void setTabs(int[] tabs) {
        synchronized (lock) {
            if (tabs == null) {
                _tabs = STD_TABS;
            } else {
                for (int i = 0, n = tabs.length - 1; i < n; i++) {
                    if (tabs[i] <= 0 || tabs[i] >= tabs[i + 1])
                        throw new IllegalArgumentException(
                                "Tab stops must be an ascending sequence of positive integers.");
                }
                _tabs = new int[tabs.length];
                System.arraycopy(tabs, 0, _tabs, 0, tabs.length);
            }
            if (_pos != 0)
                println();
        }
    }
    /**
     * Returns unity-based tab stop positions, as an ascending sequence of
     * positive integers.
     * 
     * @return Current tab stops.
     */
    public int[] getTabs() {
        return (int[]) _tabs.clone();
    }
    /**
     * Increases the indentation level by the specified amount.
     * 
     * @param i
     *            Number of columns by which to increase indentation.
     */
    @SuppressWarnings("unchecked")
    public void pushIndent(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException(
                    "Indentation must be a positive integer");
        }
        synchronized (lock) {
            _stack.push(new Integer(i));
            _indent += i;
        }
    }
    /**
     * Increases the indentation level to the next tab stop past the current
     * output position.
     */
    public void pushIndent() {
        // Indent to the nearest tab stop to the right of the current
        // indentation level, if such a tab stop exists.
        for (int i = 0, n = _tabs.length; i < n; i++)
            if (_tabs[i] > _indent) {
                pushIndent(_tabs[i] - _indent);
                return;
            }
        // Past the rightmost tab stop, indentation is one space.
        pushIndent(1);
    }
    /**
     * Restores the previous indentation level.
     * 
     * @throws IllegalStateException
     *             if the current indentation level is 0.
     */
    public void popIndent() {
        if (_stack.empty())
            throw new IllegalStateException();
        _indent -= ((Integer) _stack.pop()).intValue();
    }
    /**
     * Returns the current indentation level.
     * 
     * @return Indentation level as a character count.
     */
    public int getIndent() {
        return _indent;
    }
    /**
     * Returns the current output position (zero-based).
     * 
     * @return The output position.
     */
    public int getPosition() {
        return _pos;
    }
    /**
     * Expands a tab character by setting the output position to the next tab
     * stop past the current output position.
     * 
     * @return Space-filled string.
     */
    private String expandTab() {
        // If pos is after the last tab stop, translate tab characters to
        // spaces.
        String s = " ";
        int curpos = _indent + _pos;
        for (int i = 0; i < _tabs.length; i++) {
            // Tab stops use 1-based column numbers,
            if (_tabs[i] - 1 > curpos) {
                // curpos is a 0-based column index.
                s = spaces(_tabs[i] - curpos - 1);
                break;
            }
        }
        _pos += s.length();
        return s;
    }
    /**
     * Expands embedded tab and newline escape sequences, adjusting the output
     * position accordingly. The method recognizes 'C'/Java-style '/t', '/r' and
     * '/n' escape sequences.
     * 
     * @param ch
     *            Character to expand.
     * @return String containing (expanded) input character.
     */
    private String expandEscapes(char ch) {
        StringBuffer result = new StringBuffer();
        switch (ch) {
        case '\t':
            if (_pos == 0 && _indent > 0)
                result.append(spaces(_indent));
            result.append(expandTab());
            break;
        case '\n':
            _pos = 0;
        case '\r':
            result.append(ch);
            break;
        default:
            if (_pos == 0 && _indent > 0)
                result.append(spaces(_indent));
            result.append(ch);
            _pos++;
        }
        return result.toString();
    }
    /**
     * Expands embedded tab and newline escape sequences, adjusting the output
     * position accordingly. The method recognizes 'C'/Java-style '/t', '/r' and
     * '/n' escape sequences.
     * 
     * @param s
     *            Source string.
     * @param off
     *            Offset at which to start copying.
     * @param len
     *            Number of source characters to process.
     * @return Copy of the source string where all escape sequences have been
     *         replaced by their equivalent characters.
     */
    private String expandEscapes(String s, int off, int len) {
        StringBuffer result = new StringBuffer(len);
        for (int i = off, end = off + len; i < end; i++) {
            char ch = s.charAt(i);
            switch (ch) {
            case '\t':
                if (_pos == 0 && _indent > 0)
                    result.append(spaces(_indent));
                result.append(expandTab());
                break;
            case '\n':
                _pos = 0;
            case '\r':
                result.append(ch);
                break;
            default:
                if (_pos == 0 && _indent > 0)
                    result.append(spaces(_indent));
                result.append(ch);
                _pos++;
            }
        }
        return result.toString();
    }
    /**
     * Writes a character, which may be a tab or newline.
     * 
     * @param c
     *            Character to write.
     */
    private void _writeEx(int c) {
        String s = expandEscapes((char) c);
        super.write(s, 0, s.length());
    }
    /**
     * Writes a string which may contain tab or newline characters.
     * 
     * @param s
     *            Source string.
     * @param off
     *            Offset at which to start writing.
     * @param len
     *            Number of source characters to process.
     */
    private void _writeEx(String s, int off, int len) {
        s = expandEscapes(s, off, len);
        super.write(s, 0, s.length());
    }
    /**
     * Writes a string that does not contain embedded tabs or newlines.
     * 
     * @param s
     *            Source string.
     * @param off
     *            Offset at which to start writing.
     * @param len
     *            Number of source characters to process.
     */
    private void _write(String s, int off, int len) {
        _pos += len;
        super.write(s, off, len);
    }
    public void print(boolean b) {
        String s = String.valueOf(b);
        _write(s, 0, s.length());
    }
    public void print(char c) {
        _writeEx(c);
    }
    public void print(int i) {
        String s = String.valueOf(i);
        _write(s, 0, s.length());
    }
    public void print(long l) {
        String s = String.valueOf(l);
        _write(s, 0, s.length());
    }
    public void print(float f) {
        String s = String.valueOf(f);
        _write(s, 0, s.length());
    }
    public void print(double d) {
        String s = String.valueOf(d);
        _write(s, 0, s.length());
    }
    public void print(char[] ca) {
        _writeEx(new String(ca), 0, ca.length);
    }
    public void print(String s) {
        _writeEx(s, 0, s.length());
    }
    public void print(Object obj) {
        String s = String.valueOf(obj);
        _writeEx(s, 0, s.length());
    }
    private void newLine() {
        _write(NEWLINE, 0, NEWLINE.length());
        _pos = 0;
        if (_autoFlush)
            flush();
    }
    public void println() {
        synchronized (lock) {
            newLine();
        }
    }
    public void println(boolean b) {
        synchronized (lock) {
            print(b);
            newLine();
        }
    }
    public void println(char c) {
        synchronized (lock) {
            print(c);
            newLine();
        }
    }
    public void println(int i) {
        synchronized (lock) {
            print(i);
            newLine();
        }
    }
    public void println(long l) {
        synchronized (lock) {
            print(l);
            newLine();
        }
    }
    public void println(float f) {
        synchronized (lock) {
            print(f);
            newLine();
        }
    }
    public void println(double d) {
        synchronized (lock) {
            print(d);
            newLine();
        }
    }
    public void println(char[] c) {
        synchronized (lock) {
            print(c);
            newLine();
        }
    }
    public void println(String s) {
        synchronized (lock) {
            print(s);
            newLine();
        }
    }
    public void println(Object obj) {
        synchronized (lock) {
            print(obj);
            newLine();
        }
    }
    public void write(int c) {
        _writeEx(c);
    }
    public void write(char[] buf, int off, int len) {
        _writeEx(new String(buf, off, len), 0, len);
    }
    public void write(char[] buf) {
        _writeEx(new String(buf), 0, buf.length);
    }
    public void write(String s, int off, int len) {
        _writeEx(s, off, len);
    }
    public void write(String s) {
        _writeEx(s, 0, s.length());
    }
    public static void main(String[] args) throws Exception {
        System.out.println("please enter file path");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(
                System.in));
        String line = null;
        while ((line = stdin.readLine()) != null) {
            File dir = new File(line.trim());
            if (dir.exists()) {
                System.out.println(dir.getAbsolutePath());
                break;
            } else {
                System.out.println("dir does not exist,please re-enter:");
            }
        }
    }
}
