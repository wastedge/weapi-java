package wastedge.api.weql;

import wastedge.api.weql.syntax.AbstractSyntaxVisitor;
import wastedge.api.weql.syntax.EntityNameSyntax;
import wastedge.api.weql.syntax.IdentifierSyntax;
import wastedge.api.weql.syntax.QualifiedNameSyntax;

class NamePrinter extends AbstractSyntaxVisitor {
    private final StringBuilder sb = new StringBuilder();

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public void visitEntityName(EntityNameSyntax syntax) throws Exception {
        syntax.getQualifier().accept(this);
        sb.append('/');
        syntax.getIdentifier().accept(this);
    }

    @Override
    public void visitQualifiedName(QualifiedNameSyntax syntax) throws Exception {
        syntax.getQualifier().accept(this);
        sb.append('.');
        syntax.getIdentifier().accept(this);
    }

    @Override
    public void visitIdentifier(IdentifierSyntax syntax) throws Exception {
        sb.append(syntax.getName());
    }
}
