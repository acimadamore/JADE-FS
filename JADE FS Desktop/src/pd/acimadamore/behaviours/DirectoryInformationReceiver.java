package pd.acimadamore.behaviours;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import pd.acimadamore.agents.FileSharerAgent;
import pd.acimadamore.ontologies.SharedDirectory;
import pd.acimadamore.ontologies.SharedDirectoryOntology;

/*
 * This behaviour receives the messages sended by other Agents. 
 * The messages always contain the agent's shared directory information.
 *  
 * */
public class DirectoryInformationReceiver extends CyclicBehaviour {

  private static final long serialVersionUID = 1L;
  
  public static final String TOPIC_NAME = "shared-directory-information";

  private MessageTemplate mt;
  
  @Override
  public void onStart() {
    Codec    codec    = new SLCodec();
    Ontology ontology = SharedDirectoryOntology.getInstance();
    AID      topic    = null;  
    
    // Register to receive topic's messages
    try {
      TopicManagementHelper topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
      
      topic = topicHelper.createTopic(DirectoryInformationReceiver.TOPIC_NAME);
      
      topicHelper.register(topic);
    } 
    catch (ServiceException e) {
      e.printStackTrace();
    } 
    
    MessageTemplate mt1 = MessageTemplate.MatchLanguage(codec.getName());
    MessageTemplate mt2 = MessageTemplate.MatchOntology(ontology.getName());
    
    // Receive message because somebody is broadcasting his shared directory information or receive a response to a broadcast that the DirectoryInformationSender did
    MessageTemplate mt3 = MessageTemplate.MatchTopic(topic);
    MessageTemplate mt4 = MessageTemplate.MatchInReplyTo(DirectoryInformationReceiver.TOPIC_NAME);
    
    mt = MessageTemplate.and(MessageTemplate.and(mt1, mt2), MessageTemplate.or(mt3, mt4));
  }
  
  
  @Override
  public void action() {
    
    ACLMessage msg = myAgent.receive(mt);
    
    if(msg != null) {
      try {
        // If it was me that send this message, don't process it
        if(!wasSentByMyAgent(msg)){
          
          SharedDirectory remoteDirectory = (SharedDirectory) myAgent.getContentManager().extractContent(msg);
          
          // Save the sender's remote directory
          myAgent().addRemoteDirectory(msg.getSender(), remoteDirectory);
                 
          // If the message is not a reply it means is product of a broadcast so i have to reply with my shared directory information
          if(msg.getInReplyTo() == null){
            this.sendReply(msg);
          }
        }
      }
      catch(Exception fe) {
        fe.printStackTrace();
      }
    }
    else {
      block();
    }
  }
  
  
  private void sendReply(ACLMessage msg){
    
    ACLMessage reply = msg.createReply();
    
    reply.setPerformative(ACLMessage.CONFIRM);
    reply.setInReplyTo(DirectoryInformationReceiver.TOPIC_NAME);
    
    try {
      myAgent.getContentManager().fillContent(reply, ((FileSharerAgent)myAgent).getSharedDirectory());
    } 
    catch (CodecException | OntologyException e) {
      e.printStackTrace();
    } 
    
    myAgent.send(reply);
  }
  
  private FileSharerAgent myAgent(){
    return (FileSharerAgent) this.myAgent;
  }
  
  private Boolean wasSentByMyAgent(ACLMessage msg){
    return msg.getSender().getLocalName().equals(this.myAgent.getLocalName());
  }

}
