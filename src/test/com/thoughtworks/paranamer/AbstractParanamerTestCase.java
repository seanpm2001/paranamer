package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Method;

public abstract class AbstractParanamerTestCase extends TestCase {
    protected Paranamer paranamer;


    public void testLookupMethodReturnsNullIfMethodNotFound()
            throws IOException {
        Object method = paranamer.lookupMethod(
                Paranamer.class.getClassLoader(),
                "com.thoughtworks.paranamer.QdoxParanamerGenerator",
                "generate", "hello,goodbye");
        assertNull(method);
    }

    public void testLookupMethodReturnsNullIfClassNotFound()
            throws IOException {
        Object method = paranamer.lookupMethod(
                Paranamer.class.getClassLoader(), "paranamer.Footle",
                "generate", "sourcePath,rootPackage");
        assertNull(method);
    }

    public void testLookupParanamerCanIndicateThatUnableToGetParameterNames()
            throws IOException {
        ClassLoader cl = new ClassLoader(){};
        Object method = paranamer.lookupMethod(cl, "Blah", "doBlah", "blah");
        assertNull(method);
        int x = paranamer.isParameterNameDataAvailable(cl,"Blah", "doBlah");
        assertEquals(Paranamer.NO_PARAMETER_NAMES_LIST, x);
    }

    public void testLookupParanamerCanIndicateAbleToGetParameterNames()
            throws IOException {
        int x = paranamer.isParameterNameDataAvailable(QdoxParanamerGenerator.class.getClassLoader(),
                "com.thoughtworks.paranamer.QdoxParanamerGenerator", "generate");
        assertEquals(Paranamer.PARAMETER_NAME_DATA_FOUND, x);

    }


    public void testLookupMethodEndsWithUnknownClass() throws IOException {
        Object method = paranamer.lookupMethod(
                Paranamer.class.getClassLoader(), "foo.Bar", "generate",
                "hello,goodbye");
        assertNull(method);
    }

    public void testLookupFailsIfResourceMissing() throws IOException {
        Paranamer paranamer = new DefaultParanamer("/inexistent/resource");
        Method m = paranamer.lookupMethod(Paranamer.class.getClassLoader(),
                "com.thoughtworks.paranamer.QdoxParanamerGenerator",
                "generate", "sourcePath,rootPackage");
        assertTrue("null expected", m == null);
    }

    public void testMethodWithNoArgsCanBeRetrievedByParameterNames()
            throws IOException, NoSuchMethodException {
        Method method = paranamer.lookupMethod(
                Paranamer.class.getClassLoader(),
                "com.thoughtworks.paranamer.ParanamerGeneratorMojo", "execute", "");
        assertEquals(ParanamerGeneratorMojo.class.getMethod("execute", new Class[0]),
                method);
    }

    public void testMethodWithNoArgsCanBeRetrievedAndShowNoParameterNames()
            throws IOException, NoSuchMethodException {
        String[] choices = paranamer.lookupParameterNames(Paranamer.class
                .getClassLoader(), "com.thoughtworks.paranamer.ParanamerGeneratorMojo",
                "execute");
        assertEquals(0, choices.length);
    }

    public void testLookupParameterNamesForMethod() throws Exception {
        Method method = QdoxParanamerGenerator.class.getMethod("write", new Class[] {String.class, String.class});
        String parameters = paranamer.lookupParameterNamesForMethod(method);

        assertEquals("outputPath,content", parameters);
    }

    public void testLookupParameterNamesForMethodWhenNoArg() throws Exception {
        Method method = DefaultParanamer.class.getMethod("toString", new Class[0]);
        String parameters = paranamer.lookupParameterNamesForMethod(method);

        assertEquals("", parameters);
    }

}
