package com.nobigsoftware.dfalex;

import org.junit.Assert;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.HashSet;

public class TestBase {
    final PrettyPrinter m_printer = new PrettyPrinter();

    int _countStates(DfaState<?>... starts) {
        ArrayDeque<DfaState<?>> togo = new ArrayDeque<>();
        HashSet<DfaState<?>> checkSet = new HashSet<>();
        for (DfaState<?> start : starts) {
            if (checkSet.add(start)) {
                togo.add(start);
            }
        }
        while (!togo.isEmpty()) {
            DfaState<?> scanst = togo.removeFirst();
            scanst.enumerateTransitions((c1, c2, newstate) -> {
                if (checkSet.add(newstate)) {
                    togo.add(newstate);
                }
            });
        }
        return checkSet.size();
    }

    void _checkDfa(DfaState<?> start, String resource, boolean doStdout) throws Exception {
        String have;
        {
            StringWriter w = new StringWriter();
            m_printer.print(new PrintWriter(w), start);
            have = w.toString();
        }
        if (doStdout) {
            System.out.print(have);
            System.out.flush();
        }
        String want = _readResource(resource);
        Assert.assertEquals(want, have);
    }

    String _readResource(String resource) throws Exception {
        InputStream instream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            InputStreamReader inreader = new InputStreamReader(instream, Charset.forName("UTF-8"));
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            for (; ; ) {
                int rlen = inreader.read(buf);
                if (rlen <= 0) {
                    break;
                }
                sb.append(buf, 0, rlen);
            }
            return sb.toString();
        } finally {
            instream.close();
        }
    }
}
