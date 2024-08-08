package ru.runa.common.web;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import ru.runa.wf.web.ftl.component.EditUserTypeList;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Threads(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 10, time = 10, timeUnit =  TimeUnit.MICROSECONDS)
@Measurement(iterations = 10, time = 10, timeUnit =  TimeUnit.MICROSECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(warmups = 1, value = 1)
public class GetTemplateValueReturnTest {

    private final String stringInput = "ggfdsgfsgfdsgf\ndsgfdsgfsgfhjhlkhkj;lhfgjhg\ngfgfdgfdgfdgfdgfd[]" +
            "{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{" +
            "}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkd\"njkgf][ndjgkfndjkgnfdjk\"gnjkfdnjk";

    private final String stringOutput = "ggfdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd{}" +
            "{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{" +
            "}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkd'njkgf][ndjgkfndjkgnfdjk'gnjkfdnjk";

    private final EditUserTypeList editUserTypeList = new EditUserTypeList();

    @Test
    public void benchStart() throws IOException {
        String[] args = {};
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    @Test
    public void getStringByReplace() {
        String stringToCheck = editUserTypeList.replaceByDefaultOld(stringInput);
        Assert.assertEquals(stringToCheck, stringOutput);
    }

    @Benchmark
    @Test
    public void getStringByBuilder() {
        String stringToCheck = editUserTypeList.replaceByDefaultNew(stringInput);
        Assert.assertEquals(stringToCheck, stringOutput);
    }
}
