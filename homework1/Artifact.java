import jade.util.leap.Serializable;

public class Artifact implements Serializable {

	private int age;
	private int id;
	private String name;
	private String author;

	public Artifact(int artifactid, int artifactage, String artifactname, String artifactauthor) {
		this.id = artifactid;
		this.age = artifactage;
		this.name = artifactname;
		this.author = artifactauthor;
	}
}
