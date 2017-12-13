package pd.acimadamore.ontologies;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;


public class SharedFile implements Concept {
  
  private static final long serialVersionUID = 1L;
  
  private String name;
  private Long   size;
  
  public SharedFile(){}
  
  public SharedFile(String name){
    this.name = name;
  }
  
  public SharedFile(String name, Long size){
    this.name      = name;
    this.size      = size;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  @Slot(mandatory = false) 
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  @Override
  public String toString() {
    return name;
  }
}

