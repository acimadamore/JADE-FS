package pd.acimadamore.behaviours;

import java.util.Map;

import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;

import pd.acimadamore.agents.FileSharerAgent;

public class AgentLifeCycleEventsHandler extends AMSSubscriber {

  private static final long serialVersionUID = 1L;

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  protected void installHandlers(Map handlers) {
    // Associate an handler to born-agent events
    handlers.put(IntrospectionVocabulary.BORNAGENT, new EventHandler() {

      public void handle(Event ev) {
        BornAgent ba = (BornAgent) ev;
      }

    });

    // If an agent died i have to remove its shared directory from my agent list
    handlers.put(IntrospectionVocabulary.DEADAGENT, new EventHandler() {

      public void handle(Event ev) {
        DeadAgent da = (DeadAgent) ev;
        
        ((FileSharerAgent)myAgent).removeRemoteDirectory(da.getAgent());
      }

    });

  }

}
