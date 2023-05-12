package com.wuba.wsilk.maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;

import com.wuba.wsilk.common.Dependency;
import com.wuba.wsilk.common.Exclusion;
import com.wuba.wsilk.common.MavenPom;
import com.wuba.wsilk.common.Dependency.Scope;

import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.XPathContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * pom操作
 * 
 * @author mindashuang
 */
public class PomUpdate {

	private final String NS = "http://maven.apache.org/POM/4.0.0";

	private final String wsilkStart = "wsilk_add_start";

	private final String wsilkEnd = "wsilk_add_end";

	private final MavenProject project;

	private final String sourceEncoding;

	public PomUpdate(MavenProject project, String sourceEncoding) {
		this.project = project;
		this.sourceEncoding = sourceEncoding;
	}

	public void update() {
		List<Dependency> dependencies = MavenPom.deserialize(project.getBasedir());
		if (dependencies != null && dependencies.size() > 0) {
			save(dependencies);
		}
	}

	/**
	 * 保存依赖
	 * 
	 * @throws IOException
	 * @throws ParsingException
	 * @throws ValidityException
	 */
	public void save(List<Dependency> dependencies) {
		if (dependencies != null && dependencies.size() > 0) {
			File pomFile = new File(project.getBasedir(), "pom.xml");
			Builder builder = new Builder();
			Document doc;
			String pomContent = null;
			boolean update = false;
			try {
				pomContent = FileUtils.readFileToString(pomFile, sourceEncoding);
				doc = builder.build(pomFile);
				XPathContext context = new XPathContext("mvn", NS);
				Nodes nodes = doc.query("/mvn:project/mvn:dependencies", context);
				if (nodes.size() == 1) {
					Element node = (Element) nodes.get(0);
					Comment start = null;
					Comment end = null;
					boolean canMove = false;
					for (int i = 0; i < node.getChildCount(); i++) {
						Node child = node.getChild(i);
						if (child instanceof Comment) {// 如果是注解
							Comment comment = (Comment) child;
							String value = comment.getValue();
							if (wsilkStart.equals(value)) {
								start = comment;
								canMove = true;
							}
							if (wsilkEnd.equals(value)) {
								end = comment;
								node.removeChild(i);
								canMove = false;
							}
						}
						if (canMove) {
							node.removeChild(i);
						}
					}
					if (start == null) {
						start = new Comment(wsilkStart);
					}
					if (end == null) {
						end = new Comment(wsilkEnd);
					}
					/**
					 * 清除空格
					 */
					node.appendChild(start);
					for (Dependency dependency : dependencies) {
						Element element = create("dependency");
						create(element, dependency);
						node.appendChild(element);
					}
					node.appendChild(end);
					Serializer serializer = new Serializer(new FileOutputStream(pomFile), sourceEncoding);
					serializer.setIndent(4);
					serializer.setMaxLength(64);
					serializer.write(doc);
					update = true;
				} else {
					update = true;
				}
			} catch (ParsingException | IOException e) {
				e.printStackTrace();
			} finally {
				if (!update) {
					try {
						FileUtils.write(pomFile, pomContent, sourceEncoding, false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void create(Element element, Dependency dependency) {
		append(element, "groupId", dependency.groupId());
		append(element, "artifactId", dependency.artifactId());
		append(element, "version", dependency.version());

		if (dependency.type() != Dependency.Type.JAR) {
			append(element, "type", dependency.type().getName());
		}
		if (StringUtils.isNoneEmpty(dependency.classifier())) {
			append(element, "classifier", dependency.classifier());
		}
		if (dependency.scope() != Scope.RUNTIME) {
			append(element, "scope", dependency.scope().getName());
		}
		if (StringUtils.isNoneEmpty(dependency.systemPath())) {
			append(element, "systemPath", dependency.systemPath());
		}
		if (dependency.optional()) {
			append(element, "optional", "true");
		}
		Exclusion[] exclusions = dependency.exclusions();
		if (exclusions != null && exclusions.length > 0) {
			Element exclusionsElement = create("exclusions");
			for (Exclusion exclusion : exclusions) {
				Element exclusionElement = create("exclusion");
				append(exclusionElement, "groupId", exclusion.groupId());
				append(exclusionElement, "artifactId", exclusion.artifactId());
				exclusionsElement.appendChild(exclusionElement);
			}
			element.appendChild(exclusionsElement);
		}
	}

	private void append(Element element, String id, String value) {
		if (StringUtils.isNoneBlank(value)) {
			Element node = create(id);
			node.appendChild(value);
			element.appendChild(node);
		}
	}

	private Element create(String name) {
		return new Element(name, NS);
	}

}
