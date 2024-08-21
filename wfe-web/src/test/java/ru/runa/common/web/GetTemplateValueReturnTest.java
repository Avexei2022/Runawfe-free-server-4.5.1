package ru.runa.common.web;

import org.apache.ecs.html.S;
import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import ru.runa.wf.web.ftl.component.EditUserTypeList;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Threads(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 25, time = 10, timeUnit =  TimeUnit.MICROSECONDS)
@Measurement(iterations = 25, time = 10, timeUnit =  TimeUnit.MICROSECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(warmups = 1, value = 1)
public class GetTemplateValueReturnTest {

    private final String stringInput = "ggfdsgfsgfdsgf\ndsgfdsgfsgfhjhlkhkj;lhfgjhg\ngfgfdgfdgfdgfdgfd[]" +
            "{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{" +
            "}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkd\"njkgf][ndjgkfndjkgnfdjk\"gnjkfdnjk";

    private final String stringOutput = "ggfdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd{}" +
            "{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{" +
            "}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkd'njkgf][ndjgkfndjkgnfdjk'gnjkfdnjk";

    final static String testString1 = "ggfdsgfsgfdsgf\ndsgfdsgfsgfhjhlkhkj;lhfgjhg\ngfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkd\"njkgf][ndjgkfndjkgnfdjk\"gnjkfdnjk" +
 "ggfdsgfsgfdsgf\ndsgfdsgfsgfhjhlkhkjlhfgjhg\ngfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkd\"njkgf][ndjgkfndjkgnfdjk\"gnjkfdnjk" +
         "ggfdsgfsgfdsgfdsgfdsgfsgfhjhlkhkjlhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdg\nfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkfdnjk +ggfdsgfsgfdsgf" +
         "dsgfdsgfsgfhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndj\ngkfndjkgnfdjkgnjkfdnjk" +
         "ggfdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjh\nggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfd\ngfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfd\nsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "ggfdsgfsgfdsgfdsgfdsgfsgfhjh\"lkhkj;lhfg\"jhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrf\nvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy{}89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "ggfdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjhggfgfdgfdgf\ndgfdgfd[]{}gfdgfdgfddgfdgfdgfdgf\ndgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjk{}gnfjdkngjfkdngjfkdgnjfdkn{}jk\nngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "ggfdsgfsgfdsgfdsgfdsg\nfsgfhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfd\ngfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "ggfdsgfsgfdsgfdsgfdsgfs\ngfhjhl khkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkf\ndsy89743r843{}u\nr434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "ggfdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgj hggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgf\ndgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "ggfdfff{}{}sgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjhggfgfd\ngfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfd[]jkgnfjdkngjfkd{}ngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsgfsgfdsgfdsgfdsgfs\ngfhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfo\nhrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjf[]kdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsgfsgfdsgfdsgfdsgfsgf\nhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgn\nfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjhggfgfdgfdg\nfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsg fsgfdsgfdsgfdsgfsgfhj\nhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}g\nfdgfdgfddgfdgfdgfdgfdgfdgfdga\ndjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjk\ngnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsgfsgfdsgfdsgfdsgfsgfh jhlkh\nkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgf\"dgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}v\ngfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsgfs gfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgf\ndgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkng\njfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhf\ngjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadj\nfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhn\nfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfd{}jkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf" +
         "gg fdsgfsgfdsgfdsgfdsgfsgfhjhlkhkj;lhfgjhggfgfdgfdgfdgfdgfd[]{}gfdgfdgfddgfdgfdgfdgfdgfdgfdgadjfohrfvnjknjxvnkdfs8778943y2rhrnjgfjksdfhnfgjkfdsy89743r843{}ur434r3{}vgfdgfdndjkgnfdjkgnfjdkngjfkdngjfkdgnjfdkn{}jkngjfkdnjkgf][ndjgkfndjkgnfdjkgnjkf";

 static String testString = testString1 + testString1 + testString1 + testString1 + testString1 +testString1 +testString1 +testString1 +testString1;
 static String testString2 = testString + testString;



    private final EditUserTypeList editUserTypeList = new EditUserTypeList();

    @Test
    public void benchStart() throws IOException {
        String[] args = {};
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    @Test
    public void getStringByReplace() {
        String stringToCheck = editUserTypeList.replaceByDefaultOld(testString2);
//        Assert.assertEquals(stringToCheck, stringOutput);
    }

    @Benchmark
    @Test
    public void getStringByUsersMethod1() {
        String stringToCheck = editUserTypeList.replaceByDefaultNew(testString2);
//        Assert.assertEquals(stringToCheck, stringOutput);
    }

    @Benchmark
    @Test
    public void getStringByUsersMethod2() {
        String stringToCheck = editUserTypeList.replaceByDefaultNew2(testString2);
//        Assert.assertEquals(stringToCheck, stringOutput);
    }

}
