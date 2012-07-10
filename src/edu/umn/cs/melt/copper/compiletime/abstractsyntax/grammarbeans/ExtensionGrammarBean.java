package edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import edu.umn.cs.melt.copper.compiletime.abstractsyntax.grammarbeans.visitors.CopperASTBeanVisitor;

/**
 * Represents an "extension grammar" &#8210; a grammar that is meant to be
 * attached to another grammar and that matches the form to be passed
 * to Copper's composability checker (modular determinism analysis).
 * 
 * An extension grammar contains two additional fields, {@link #markingTerminals}
 * and {@link #bridgeProductions}, which must be non-empty before an extension grammar is
 * passed to the compiler.
 * 
 * @author August Schwerdfeger &lt;<a href="mailto:schwerdf@cs.umn.edu">schwerdf@cs.umn.edu</a>&gt;
 *
 */
public class ExtensionGrammarBean extends GrammarBean
{
	private Hashtable<CopperElementName,GrammarElementBean> bridgeElements;
	/**
	 * The grammar's marking terminals. Each production in {@code bridgeProductions}
	 * must have one of these as their first right-hand-side symbol, and they may
	 * not be used anywhere else.
	 */
	protected Set<CopperElementName> markingTerminals;
	/** The start productions of the grammar, which must be of the form
	 * <code>H ::= m E</code>, where:
	 * <ul><li><code>H</code> is the name of a nonterminal
	 * in the "host" language;</li><li><code>m</code> is the name of
	 * <code>markingTerminal</code>; and </li><li><code>E</code> is this
	 * grammar's start symbol.</li></ul>
	 * The start production should not be placed in the
	 * grammar's list of productions.
	 */
	protected Set<CopperElementName> bridgeProductions;
	
	public ExtensionGrammarBean()
	{
		super(CopperElementType.EXTENSION_GRAMMAR);
		markingTerminals = new HashSet<CopperElementName>();
		bridgeProductions = new HashSet<CopperElementName>();
		bridgeElements = new Hashtable<CopperElementName,GrammarElementBean>();
	}
	
	/**
	 * Fills this extension grammar with the elements of another grammar.
	 * Meant for the straightforward construction of several different
	 * extensions from a single grammar, as when a single extension is used
	 * with several host languages.
	 *  
	 * N.B.: This will make a shallow copy &#8210; any changes to the elements
	 * of the original grammar will carry over to this grammar,
	 * and vice versa.
	 */
	public void fill(GrammarBean grammar)
	{
		grammarElements = grammar.grammarElements;
		grammarLayout = grammar.grammarLayout;		
	}
	
	
	/**
	 * @see #markingTerminals 
	 */
	public Set<CopperElementName> getMarkingTerminals()
	{
		return markingTerminals;
	}
	
	/**
	 * @see #markingTerminals 
	 */
	public TerminalBean getMarkingTerminal(CopperElementName name)
	{
		if(!markingTerminals.contains(name)) return null;
		return (TerminalBean) bridgeElements.get(name);
	}

	/**
	 * @see #bridgeProductions
	 */
	public Set<CopperElementName> getBridgeProductions()
	{
		return bridgeProductions;
	}
	
	/**
	 * @see #bridgeProductions
	 */
	public ProductionBean getBridgeProduction(CopperElementName name)
	{
		if(!bridgeProductions.contains(name)) return null;
		return (ProductionBean) bridgeElements.get(name);
	}

	/**
	 * @see #markingTerminals
	 */
	public boolean addMarkingTerminal(TerminalBean terminal)
	{
		if(bridgeElements.containsKey(terminal.getName())) return false;
		bridgeElements.put(terminal.getName(),terminal);
		markingTerminals.add(terminal.getName());
		return true;
	}
	
	/**
	 * @see #bridgeProduction
	 */
	public boolean addBridgeProduction(ProductionBean production)
	{
		if(bridgeElements.containsKey(production.getName())) return false;
		bridgeElements.put(production.getName(),production);
		markingTerminals.add(production.getName());
		return true;
	}
	
	@Override
	/**
	 * @see CopperASTBean#isComplete(), GrammarBean#isComplete()
	 */
	public boolean isComplete()
	{
		return super.isComplete() && (!markingTerminals.isEmpty() && !bridgeProductions.isEmpty());
	}
	
	@Override
	/**
	 * @see CopperASTBean#whatIsMissing()
	 */
	public Set<String> whatIsMissing()
	{
		Set<String> rv = super.whatIsMissing();
		if(markingTerminals.isEmpty()) rv.add("markingTerminals");
		if(bridgeProductions.isEmpty()) rv.add("bridgeProductions");
		return rv;
	}
	
	public <RT,E extends Exception> RT acceptVisitor(CopperASTBeanVisitor<RT,E> visitor)
	throws E
	{
		return visitor.visitExtensionGrammarBean(this);
	}	
}
