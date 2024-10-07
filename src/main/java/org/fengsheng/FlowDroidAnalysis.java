package org.fengsheng;

import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;

import java.util.Map;
import java.util.Set;

public class FlowDroidAnalysis {
    //这里的代码暂时跑不通，还在完善休整

    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.err.println("Usage: java -jar flowdroid-analysis.jar <apk-file> <android-platforms-directory>");
//            System.exit(1);
//        }

        String androidPlatformPath = "/Users/luochang/Library/Android/sdk/platforms";
        //设置要分析的APK文件
        String apkPath = "/Users/luochang/IDEAProject/FlowDroidTest/src/main/resources/tangtang.apk";

        try {
            SetupApplication app = new SetupApplication(androidPlatformPath, apkPath);

            // Optional: Configure additional options
            app.setCallbackFile("AndroidCallbacks.txt"); // Ensure this file is available in the resources
            app.getConfig().setImplicitFlowMode(InfoflowConfiguration.ImplicitFlowMode.AllImplicitFlows);
            app.getConfig().setInspectSources(true);

            // Run the data flow analysis
            InfoflowResults results = app.runInfoflow();

            // Process and print the results
            if (results == null || results.isEmpty()) {
                System.out.println("No data leaks found.");
            } else {
                System.out.println("Found data leaks:");
                for (ResultSourceInfo source : results.getResults().values()) {
                    Set<ResultSinkInfo> sinks = results.getResults().keySet();
                    for (ResultSinkInfo sink : sinks) {
                        System.out.println("Leak from source:");
                        printSourceInfo(source);
                        System.out.println("To sink:");
                        printSinkInfo(sink);
                        System.out.println();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printSourceInfo(ResultSourceInfo source) {
//        System.out.println("  - Source method: " + source.get);
        System.out.println("  - Source statement: " + source.getStmt());
        System.out.println("  - Source line number: " + source.getStmt().getJavaSourceStartLineNumber());
    }

    private static void printSinkInfo(ResultSinkInfo sink) {
//        System.out.println("  - Sink method: " + sink.getMethod());
        System.out.println("  - Sink statement: " + sink.getStmt());
        System.out.println("  - Sink line number: " + sink.getStmt().getJavaSourceStartLineNumber());
    }
}