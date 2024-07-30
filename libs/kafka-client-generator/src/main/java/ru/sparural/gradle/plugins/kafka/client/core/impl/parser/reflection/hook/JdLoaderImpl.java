package ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.hook;

import lombok.RequiredArgsConstructor;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class JdLoaderImpl implements Loader {
    private final Class<?> cl;

    @Override
    public boolean canLoad(String internalName) {
        return cl.getResource(".class") != null;
    }

    @Override
    public byte[] load(String internalName) throws LoaderException {
        InputStream is = cl.getResourceAsStream(cl.getSimpleName() + ".class");

        if (is == null) {
            return null;
        } else {
            try (InputStream in = is; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);

                while (read > 0) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }

                return out.toByteArray();
            } catch (IOException e) {
                throw new LoaderException(e);
            }
        }
    }
}
