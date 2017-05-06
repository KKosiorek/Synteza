package pl.edu.wat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import javax.tools.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        final String fileName = "src\\Class.java";
        final String alteredFileName = "src\\ClassAltered.java";
        CompilationUnit cu;
        try(FileInputStream in = new FileInputStream(fileName)){
            cu = JavaParser.parse(in);
        }

        cu.getNodesByType(MethodDeclaration.class)
			.stream()
			.forEach(Main::weaveLog);

//        new Rewriter().visit(cu, null);
        cu.getClassByName("Class").get().setName("ClassAltered");

        try(FileWriter output = new FileWriter(new File(alteredFileName), false)) {
            output.write(cu.toString());
        }

        File[] files = {new File(alteredFileName)};
        String[] options = { "-d", "out//production//Synthesis" };

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
            compiler.getTask(
                null,
                fileManager,
                diagnostics,
                Arrays.asList(options),
                null,
                compilationUnits).call();

            diagnostics.getDiagnostics().forEach(d -> System.out.println(d.getMessage(null)));
        }
    }

    private static BlockStmt GetMethodStmt(MethodDeclaration method){
        BlockStmt block;
        Optional<BlockStmt> body = method.getBody();
        if (!body.isPresent()){
            block = new BlockStmt();
            method.setBody(block);
        }
        else {
            block = body.get();
        }

        return block;
    }

    private static void weaveLog(MethodDeclaration method) {

        Type type = method.getType();
        if(!type.toString().equals("void")) {
            BlockStmt block = GetMethodStmt(method);
            NodeList<Statement> statements = block.getStatements();
            List<Statement> returnStatements = new LinkedList<>();
            for (Statement st:statements
                 ) {
                    if(st.toString().startsWith("return")) {
                        returnStatements.add(st);
                    }

            }
            if (!returnStatements.isEmpty()) {
                for (Statement returnStatement:returnStatements
                     ) {
                    MethodCallExpr call = new MethodCallExpr(null, "Logger.Log");
                    call.addArgument(new StringLiteralExpr(method.getNameAsString()));
                    call.addArgument((Expression) returnStatement.getChildNodes().get(0));
                    block.addStatement(statements.indexOf(returnStatement), call);
                }
            }
        }
    }

//    private static class Rewriter extends VoidVisitorAdapter<Void> {
//        @Override
//        public void visit(MethodDeclaration n, Void arg) {
//            String methodName = n.getNameAsString();
//            if ("log".equalsIgnoreCase(methodName))
//                return;
//
//            BlockStmt block = GetMethodStmt(n);
//
//            Expression call = JavaParser.parseExpression("log(\"" + n.getNameAsString() + "\")");
//            block.addStatement(0, call);
//        }
//    }
}
