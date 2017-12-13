package pd.acimadamore.behaviours;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import pd.acimadamore.ontologies.SharedDirectory;
import pd.acimadamore.ontologies.SharedDirectoryOntology;

/*
 * This behaviour broadcast a message to all agents through the 'shared-directory-information' topic  
 * with its agent shared directory information.
 * 
 * */
public class DirectoryInformationSender extends OneShotBehaviour {

  private static final long serialVersionUID = 1L;
  
  public static final String TOPIC_NAME = "shared-directory-information";
  
  private Codec    codec       = new SLCodec();
  private Ontology ontology    = SharedDirectoryOntology.getInstance(); 
  private AID      topic;
  
  
  private SharedDirectory sharedDirectoryInformation;
  
  public DirectoryInformationSender(SharedDirectory sharedDirectoryInformation){
    this.sharedDirectoryInformation = sharedDirectoryInformation;
  }
  
  @Override
  public void onStart() {

    try {
      TopicManagementHelper topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
      
      this.topic = topicHelper.createTopic(DirectoryInformationSender.TOPIC_NAME); 
    } 
    catch (ServiceException e1) {
      e1.printStackTrace();
    } 
  }
  
  @Override
  public void action() {
       
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    
    msg.addReceiver(this.topic);
    msg.setLanguage(this.codec.getName());
    msg.setOntology(this.ontology.getName());
    
    try {
      myAgent.getContentManager().fillContent(msg, this.sharedDirectoryInformation);
    } 
    catch (CodecException | OntologyException e) {
      e.printStackTrace();
    }     
    
    myAgent.send(msg);
  }

}
