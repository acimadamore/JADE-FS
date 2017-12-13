package pd.acimadamore.ontologies;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jade.content.Predicate;
import jade.core.Location;
import pd.acimadamore.ontologies.SharedFile;

public class SharedDirectory implements Predicate {

  private static final long serialVersionUID = 1L;

  private String           location;
  private String           path;
  private List<SharedFile> files;

  public SharedDirectory(){

  }

  public SharedDirectory(String path, String location){
    this.location = location;
    this.path     = path;
  }

  public SharedDirectory(String path, String location, List<SharedFile> files){
    this.location = location;
    this.path     = path;
    this.files    = files;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<SharedFile> getFiles(){
    return files;
  }

  public void setFiles(List<SharedFile> files){
    this.files = files;
  }

  @Override
  public String toString() {

    StringBuffer sb = new StringBuffer();
    sb.append("@" + this.location);
    sb.append(this.path);
    sb.append("\n");

    for(SharedFile f : this.files)
      sb.append("- " + f.toString() + "\n");

    return sb.toString();
  }

  public SharedFile getFile(String filename){
    for(SharedFile f: this.files){
      if(f.getName().equals(filename)){
        return f;
      }
    }

    return null;
  }

  public static SharedDirectory createFromPathAndLocation(File path, Location location){
     /*
    Old Method to trasverse the path using Files' walkFileTree() method. Not supported until Android's API 28

    final List<SharedFile> files    = new ArrayList<SharedFile>();
    final Path       rootPath = path;

    try {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          files.add(SharedFile.createFromPathAndFileAttributes(file, attrs));

          return FileVisitResult.CONTINUE;
        }


        @Override
        public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException {
          if(directory.equals(rootPath)){
            return FileVisitResult.CONTINUE;
          }

          return FileVisitResult.SKIP_SUBTREE;
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    */

    return new SharedDirectory(path.toString(), location.getName(), getFilesFromDirectory(path));
  }

  private static List<SharedFile> getFilesFromDirectory(File path){
    List<SharedFile> lsf = new ArrayList<SharedFile>();

    for(File file : path.listFiles()){
      if(file.isDirectory()){
        lsf.addAll(getFilesFromDirectory(file));
      }
      else{
        lsf.add(new SharedFile(file.getName(), file.length()));
      }
    }

    return lsf;
  }
}
