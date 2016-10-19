package demo;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;

/*
 * @author Oleksandr Radchykov
 */
public class DemoPlugin implements Plugin {
    @Override
    public String getName() {
        return "DemoPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent e) {
                // do nothing
            }

            @Override
            public void finished(TaskEvent e) {
                if (TaskEvent.Kind.PARSE.equals(e.getKind())) {
                    System.out.println("Before: " + e.getCompilationUnit());

                    new GetterGenerator(((BasicJavacTask) task)).generateFor(e.getCompilationUnit());

                    System.out.println("After: " + e.getCompilationUnit());
                }
            }
        });
    }
}
