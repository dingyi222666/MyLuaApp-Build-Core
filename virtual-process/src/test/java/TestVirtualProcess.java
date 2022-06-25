import static org.junit.Assert.assertEquals;

import com.dingyi.virtualprocess.VirtualProcess;
import com.dingyi.virtualprocess.VirtualProcessExecutor;

import org.junit.Test;

public class TestVirtualProcess {

    @Test
    public void testSimpleRunProcess() {

        VirtualProcess testProcess = new VirtualProcess();

        testProcess.setExecutor(new TestExecutor(testProcess));

        testProcess.start();

        int exitValue = testProcess.waitFor();

        assertEquals(0, exitValue);
    }
}


class TestExecutor extends VirtualProcessExecutor {

    public TestExecutor(VirtualProcess process) {
        super(process);
    }

    @Override
    public int runInProcess() {
        System.out.println("runInProcess");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
