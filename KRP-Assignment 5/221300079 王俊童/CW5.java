package owlapi.msccourse.query;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;


/**
 * Nanjing University<br>
 * School of Artificial Intelligence<br>
 * KRistal Group<br>
 * 
 * Acknowledgement: with great thanks to Nico at Cambridge for the insightful discussions and his useful suggestions in making this project. 
 *
 */

public class CW5 {

	final OWLOntologyManager man;
	final OWLDataFactory df = OWLManager.getOWLDataFactory();
	final OWLOntology o;
	OWLReasoner r;

	CW5(File file) throws OWLOntologyCreationException {
		// DO NOT CHANGE
		this.man = OWLManager.createOWLOntologyManager();
		this.o = man.loadOntologyFromOntologyDocument(file);
		this.r = new PelletReasonerFactory().createReasoner(o);
		this.r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	}
    
    public Set<QueryResult> performQuery(OWLClassExpression exp, QueryType type) {
        System.out.println("-----------------Performing Query-----------------");
        Set<QueryResult> results = new HashSet<>();
    
        switch (type) {
            case EQUIVALENTCLASSES:
                Set<OWLClass> equivalentClasses = r.getEquivalentClasses(exp).getEntities();
                for (OWLClass cls : equivalentClasses) {
                    if (!cls.isAnonymous() && !cls.equals(df.getOWLNothing())) {
                        results.add(new QueryResult(cls, true, type));
                    }
                }
                break;
    
            case INSTANCES:
                // Direct instances
                Set<OWLNamedIndividual> directInstances = r.getInstances(exp, true).getFlattened();
                for (OWLNamedIndividual ind : directInstances) {
                    results.add(new QueryResult(ind, true, type));
                }
    
                // Indirect instances
                Set<OWLNamedIndividual> indirectInstances = r.getInstances(exp, false).getFlattened();
                indirectInstances.removeAll(directInstances);
                for (OWLNamedIndividual ind : indirectInstances) {
                    results.add(new QueryResult(ind, false, type));
                }
                break;
    
            case SUBCLASSES:
                // Direct subclasses
                Set<OWLClass> directSubclasses = r.getSubClasses(exp, true).getFlattened();
                for (OWLClass cls : directSubclasses) {
                    if (!cls.isAnonymous() && !cls.equals(df.getOWLNothing())) {
                        results.add(new QueryResult(cls, true, type));
                    }
                }
    
                // Indirect subclasses
                Set<OWLClass> indirectSubclasses = r.getSubClasses(exp, false).getFlattened();
                indirectSubclasses.removeAll(directSubclasses);
                for (OWLClass cls : indirectSubclasses) {
                    if (!cls.isAnonymous() && !cls.equals(df.getOWLNothing())) {
                        results.add(new QueryResult(cls, false, type));
                    }
                }
                break;
    
            default:
                break;
        }
    
        return results;
    }

    public boolean isValidPizza(OWLClassExpression exp) {
        OWLClass pizza = df.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#Pizza"));
        return r.getSubClasses(pizza, false).containsEntity(exp.asOWLClass());
    }

    public Set<QueryResult> filterNamedPizzas(Set<QueryResult> results) {
        OWLClass np = df.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#NamedPizza"));
        Set<QueryResult> results_filtered = new HashSet<>();
        for (QueryResult qr : results) {
            if (qr.getEntity() instanceof OWLClass && r.getSuperClasses(qr.getEntity().asOWLClass(), false).containsEntity(np)) {
                results_filtered.add(qr);
            }
        }
        return results_filtered;
    }

    public Set<OWLClassExpression> getAllSuperclassExpressions(OWLClass ce) {
        Set<OWLClassExpression> restrictions = new HashSet<>();
        NodeSet<OWLClass> superClassesNodeSet = r.getSuperClasses(ce, false); 
        Set<OWLClass> superClasses = superClassesNodeSet.getFlattened();
        superClasses.addAll(r.getEquivalentClasses(ce).getEntities()); 
        
        for (OWLClass sup : superClasses) {
            for (OWLSubClassOfAxiom ax : r.getRootOntology().getSubClassAxiomsForSuperClass(sup)) {
                OWLClassExpression superClassExpression = ax.getSuperClass();
                OWLClassExpression subClassExpression = ax.getSubClass();
                
                if (!subClassExpression.equals(sup)) {
                    restrictions.add(superClassExpression);
                }
            }
        }
        
        return restrictions;
    }

	public OWLClassExpression parseClassExpression(String sClassExpression) {
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(
				new BidirectionalShortFormProviderAdapter(man, o.getImportsClosure(), new SimpleShortFormProvider()));
		ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
		parser.setOWLEntityChecker(entityChecker);
		parser.setStringToParse(sClassExpression);
		// j
		OWLClassExpression exp = parser.parseClassExpression();
		return exp;
	}

}
