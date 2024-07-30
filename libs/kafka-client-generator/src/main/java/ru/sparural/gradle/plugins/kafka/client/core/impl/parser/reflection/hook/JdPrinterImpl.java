package ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.hook;

public class JdPrinterImpl implements JdPrinter {

    private static final String TAB = "\t";
    private static final String NEWLINE = "\n";
    private final StringBuilder sb = new StringBuilder();
    private int indentationCount = 0;

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public void start(int maxLineNumber, int majorVersion, int minorVersion) {
    }

    @Override
    public void end() {
    }

    @Override
    public void printText(String text) {
        sb.append(text);
    }

    @Override
    public void printNumericConstant(String constant) {
        sb.append(constant);
    }

    @Override
    public void printStringConstant(String constant, String ownerInternalName) {
        sb.append(constant);
    }

    @Override
    public void printKeyword(String keyword) {
        sb.append(keyword);
    }

    @Override
    public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
        sb.append(name);
    }

    @Override
    public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
        sb.append(name);
    }

    @Override
    public void indent() {
        this.indentationCount++;
    }

    @Override
    public void unindent() {
        this.indentationCount--;
    }

    @Override
    public void startLine(int lineNumber) {
        sb.append(TAB.repeat(Math.max(0, indentationCount)));
    }

    @Override
    public void endLine() {
        sb.append(NEWLINE);
    }

    @Override
    public void extraLine(int count) {
        while (count-- > 0) sb.append(NEWLINE);
    }

    @Override
    public void startMarker(int type) {
    }

    @Override
    public void endMarker(int type) {
    }

    @Override
    public JdPrinter append(String packageName) {
        sb.append(packageName);
        return this;
    }
}
