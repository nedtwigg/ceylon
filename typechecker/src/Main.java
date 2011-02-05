

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;

import com.redhat.ceylon.compiler.parser.CeylonLexer;
import com.redhat.ceylon.compiler.parser.CeylonParser;
import com.redhat.ceylon.compiler.tree.Builder;
import com.redhat.ceylon.compiler.tree.Node;
import com.redhat.ceylon.compiler.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.tree.Visitor;
import com.redhat.ceylon.compiler.tree.Walker;

public class Main {

	public static void main(String[] args) throws Exception {
		String path;
		if ( args.length==0 ) {
			path = "corpus";
		}
		else {
			path = args[0];
		}
		fileOrDir( new File(path) );
	}
	
	private static void file(File file) throws Exception {
		if ( file.getName().endsWith(".ceylon") ) {
		
		System.out.println("Parsing " + file.getName());
        InputStream is = new FileInputStream( file );
        ANTLRInputStream input = new ANTLRInputStream(is);
        CeylonLexer lexer = new CeylonLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CeylonParser parser = new CeylonParser(tokens);
        CeylonParser.compilationUnit_return r = parser.compilationUnit();

        CommonTree t = (CommonTree)r.getTree();
        
        CompilationUnit cu = new Builder().buildCompilationUnit(t);
        Visitor v = new Visitor() {
            int depth=0;
            //boolean inType=false;

            void print(String str) {
            	System.out.print(str);
            }

            void newline() {
                print("\n");
            }

            void indent() {
                for (int i = 0; i < depth; i++)
                    print("|  ");
            }
            
            /*@Override
            public void visit(Type node) {
            	if (!inType) {
                    if (depth>0) newline();
                    indent();
                    print("+ ");
            	}
                if (node.getTypeName()!=null) {
                	print(node.getTypeName().getText());
                	boolean oldInType = inType;
                	inType=true;
                	if (node.getTypeArgumentList()!=null)
                        visit(node.getTypeArgumentList());
                	inType=oldInType;
                }
            }
            
            @Override
            public void visit(TypeArgumentList node) {
            	print("<");
                super.visitAny(node);
                print(">");
            }*/
            
            @Override
            public void visitAny(Node node) {
                if (depth>0) newline();
                indent();
                print("+ ");
                print(node.getText() + " (" + node.getTreeNode().getLine() + ":" + node.getTreeNode().getCharPositionInLine()  + ")");
                depth++;
                super.visitAny(node);
                depth--;
                if (depth==0) newline();
            }
        };
        Walker.walkCompilationUnit(v, cu);

        if (lexer.getNumberOfSyntaxErrors() != 0) {
            System.out.println("Lexer failed");
        }
        else if (parser.getNumberOfSyntaxErrors() != 0) {
        	System.out.println("Parser failed");
        }
        else {
        	System.out.println("Parser succeeded");
        }
        
		}
        
	}
	
	private static void fileOrDir(File file) throws Exception {
		if (file.isDirectory()) 
			dir(file);
		else
			file(file);
	}

	private static void dir(File dir) throws Exception {
		for (File file: dir.listFiles()) 
			fileOrDir(file);
	}

}
