NPlugins
=====
Informations about the project:  
	http://dev.bukkit.org/bukkit-plugins/ncore/

Jenkins server:  
	http://ci.ribesg.fr/

Interacting with the NPlugins suite:
```xml
	...
	<repositories>
		<repository>
			<id>ribesg-releases</id>
			<name>Ribesg's Release Repository</name>
			<url>http://repo.ribesg.fr/content/repositories/releases</url>
		</repository>
		<repository>
			<id>ribesg-snapshots</id>
			<name>Ribesg's Snapshot Repository</name>
			<url>http://repo.ribesg.fr/content/repositories/snapshots</url>
		</repository>
	</repositories>

	<dependencies>
		<dependency>
			<groupId>fr.ribesg.bukkit.ncore</groupId>
			<artifactId>NCore</artifactId>
			<version>0.6.2-SNAPSHOT</version>
		</dependency>
	</dependencies>
	...
```

Then you can just listen to available events, or if you want to use some Node API, wait ~5 ticks then get it from the Core.