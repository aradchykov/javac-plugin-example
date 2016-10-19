package demo;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.stream.Collectors;

import static com.sun.tools.javac.util.List.nil;

/*
 * @author Oleksandr Radchykov
 */
class GetterGenerator {

    private final TreeMaker treeMaker;

    private final Names names;

    GetterGenerator(BasicJavacTask task) {
        Context context = task.getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
    }


    void generateFor(CompilationUnitTree compilationUnit) {
        compilationUnit.getTypeDecls().stream()
                .filter(tree -> tree instanceof JCTree.JCClassDecl)
                .map(tree -> ((JCTree.JCClassDecl) tree))
                .forEach(classDecl -> classDecl.defs = classDecl.defs.appendList(generateGettersFor(classDecl.defs)));
    }

    private List<JCTree> generateGettersFor(List<JCTree> defs) {
        return List.from(defs.stream().filter(tree -> tree instanceof JCTree.JCVariableDecl)
                .map(tree -> ((JCTree.JCVariableDecl) tree))
                .map(this::createGetter)
                .collect(Collectors.toList()));
    }

    private JCTree.JCMethodDecl createGetter(JCTree.JCVariableDecl jcVariableDecl) {
        Name methodName = names.fromString("get" + Character.toUpperCase(jcVariableDecl.name.charAt(0)) + jcVariableDecl.name.toString().substring(1));
        JCTree.JCExpression returnExpr = jcVariableDecl.vartype;

        // create body
        JCTree.JCIdent varIdentifier = treeMaker.Ident(jcVariableDecl.name);
        JCTree.JCReturn returnStmt = treeMaker.Return(varIdentifier);
        JCTree.JCBlock methodBody = treeMaker.Block(0, List.of(returnStmt));

        return createMethod(methodName, methodBody, returnExpr);
    }

    private JCTree.JCMethodDecl createMethod(Name name, JCTree.JCBlock body, JCTree.JCExpression retType) {
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC), name, retType,
                nil(), nil(), nil(),
                body, null
        );
    }

}
