package org.fengsheng;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;

public class CGGenerator {
    //设置android的jar包目录
    public final static String androidPlatformPath = "/Users/luochang/Library/Android/sdk/platforms";
    //设置要分析的APK文件
    public final static String appPath = "/Users/luochang/IDEAProject/FlowDroidTest/src/main/resources/ColumnsGembira.apk";
    public final static String outputPath = "/Users/luochang/IDEAProject/FlowDroidTest/src/main/resources/output";
    static Object ob = new Object();

    private static Map<String, Boolean> visited = new HashMap<>();
    private static CGExporter cge = new CGExporter();

    public static void main(String[] args) {
        SetupApplication app = new SetupApplication(androidPlatformPath, appPath);
        soot.G.reset();
        //传入AndroidCallbacks文件
        app.setCallbackFile(CGGenerator.class.getResource("/AndroidCallbacks.txt").getFile());
        app.constructCallgraph();
        //SootMethod 获取函数调用图
        SootMethod entryPoint = app.getDummyMainMethod();
        CallGraph cg = Scene.v().getCallGraph();
//        //可视化函数调用图
        visit(cg, entryPoint);
        //导出函数调用图
        cge.exportMIG("flowdroidCFG", outputPath);
    }

    //可视化函数调用图的函数
    private static void visit(CallGraph cg, SootMethod m) {
        //在soot中，函数的signature就是由该函数的类名，函数名，参数类型，以及返回值类型组成的字符串
        String identifier = m.getSignature();
        //记录是否已经处理过该点
        visited.put(identifier, true);
        //以函数的signature为label在图中添加该节点
        cge.createNode(identifier);
        //获取调用该函数的函数
        Iterator<MethodOrMethodContext> ptargets = new Targets(cg.edgesInto(m));
        if (ptargets != null) {
            while (ptargets.hasNext()) {
                SootMethod p = (SootMethod) ptargets.next();
                if (p == null) {
                    System.out.println("p is null");
                }
                if (!visited.containsKey(p.getSignature())) {
                    visit(cg, p);
                }
            }
        }
        //获取该函数调用的函数
        Iterator<MethodOrMethodContext> ctargets = new Targets(cg.edgesOutOf(m));
        if (ctargets != null) {
            while (ctargets.hasNext()) {
                SootMethod c = (SootMethod) ctargets.next();
                if (c == null) {
                    System.out.println("c is null");
                }
                //将被调用的函数加入图中
                cge.createNode(c.getSignature());
                //添加一条指向该被调函数的边
                cge.linkNodeByID(identifier, c.getSignature());
                if (!visited.containsKey(c.getSignature())) {
                    //递归
                    visit(cg, c);
                }
            }
        }
    }
}

