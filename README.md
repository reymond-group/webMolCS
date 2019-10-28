# webMolCS: Visualizing Molecules in 3D Chemical Space

The concept of chemical space provides a convenient framework to analyze large collections 
of molecules by placing them in property spaces where distances represent similarities. 
Here we report webMolCS, a new type of web-based interface visualizing up to 5000 user-defined
molecules in six different three-dimensional (3D) chemical spaces obtained by principal component
analysis or similarity mapping of multidimensional property spaces describing composition 
(MQN: 42D molecular quantum numbers, SMIfp: 34D SMILES fingerprint), shapes and pharmacophores
(APfp: 20D atom pair fingerprint, Xfp: 55D category extended atom pair fingerprint), 
and substructures (Sfp: 1024D binary substructure fingerprint, ECfp4:1024D extended connectivity fingerprint).
Each molecule is shown as a sphere, and its structure appears on mouse over. 
The sphere is color-coded by similarity to the first compound in the list, by the list rank, 
or by a user-defined value, which reveals the relationship between any property encoded by 
these values and structural similarities. WebMolCS website is freely available at www.gdb.unibe.ch.

## *Description of Files*

### *webMolCS*

Complete webMolCS web application

### *WebMolCS_NetBeansSource*

Netbeans project containing Java source code for Molecule Preprocessing, 
Principal component analysis and 3D-map generation. 
This Java Project is use by webMolCS application (see webMolCS/dist folder)

## *Dependencies*

1) Java
2) tomcat server
3) Chemaxon license file

## *Setting up the webMolCS in Dockerised container*

Edit the path "ABSOLUTE_PATH_TO_WEBMOLCS" in runDockerContainer.sh file.
run ./runDockerContainer.sh





