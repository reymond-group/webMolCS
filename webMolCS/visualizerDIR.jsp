<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="error.html" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileReader"%>
<%@ page import="java.io.BufferedReader"%>

<html lang="en">
<head>
<link rel="shortcut icon"  href="2.ico"/>
		<title id="title1"></title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
		<script src="FileSaver.js-master/FileSaver.js"></script>

		<style>

			body {
				font-family: Monospace;
				margin: 0px;
				overflow: hidden;
				background: black;
			}

			.info {
				font-family: Monospace;
				position: absolute;
				background-color: black;
				font-size: 20px;
				opacity: 0.8;
				color: white;
				text-align: center;
				top: 0px;
				width: 100%;
			}

			.info a {
				color: #00ffff;
			}

			#wrapper {
  				margin-right: 200px;
			}

			#container {
				float: left;
				width: 100%;
				height: 100%;
				background-color: black;
			}

			#sidebar {

				background: black;
                                background-color:rgba(0,0,0,0.5);
                                font-family: Monospace;
                                color: #ffffff;
                                position: absolute;
                                width:200px;
                                height:220px;
                                left: 0%;
                                resize:both;
                                overflow:auto;
                                border-style:solid;
                                border-color:grey;
			}

			#cleared {
				clear: both;
			}

			label{
     				display: block; 
     				height: 35px; 
     				line-height: 35px; 
     				border: 1px solid #000; 
			}

                        button {
                                color: rgba(127,255,255,0.75);
                                background:green;
                                outline: 1px solid rgba(127,255,255,0.75);
                                border: 0px;
                                padding: 5px 10px;
                                cursor: pointer;
                                color: #ffffff;
                                font-size: 12px;
                                font-family: Arial;
                        }

			button2 {
				color: rgba(127,255,255,0.75);
                                outline: 0px solid rgba(127,255,255,0.75);
                                border: 0px;
                                padding: 5px 10px;
                                #cursor: pointer;
                                color: #ffffff;
                                font-size: 12px;
                                font-family: Arial;
			}

                        button:hover {
                                background:transparent;
                                outline: 1px solid #00FF00;
                        }

                        button:active {
                                color: #000000;
                                background-color: rgba(0,255,255,0.75);
                        }

			.modal {

				display: none; /* Hidden by default */
    				position: fixed; /* Stay in place */
    				z-index: 1; /* Sit on top */
    				padding-top: 100px; /* Location of the box */
    				left: 0;
    				top: 0;
    				width: 100%; /* Full width */
    				height: 100%; /* Full height */
    				overflow: auto; /* Enable scroll if needed */
    				background-color: rgb(0,0,0); /* Fallback color */
    				background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
			}

			/* Modal Content */
			.modal-content {

				position: relative;
    				background-color: black;
    				margin: auto;
    				padding: 0;
    				border: 5px solid white;
    				width: 80%;
				height: 80%;
    				box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19);
    				-webkit-animation-name: animatetop;
    				-webkit-animation-duration: 0.4s;
    				animation-name: animatetop;
    				animation-duration: 0.4s;
				overflow: auto
			}

			/* Add Animation */
			@-webkit-keyframes animatetop {
    			from {top:-300px; opacity:0}
    			to {top:0; opacity:1}
			}

			@keyframes animatetop {
    			from {top:-300px; opacity:0}
    			to {top:0; opacity:1}
			}
	
		</style>
</head>
<body ondblclick="stop()" bgcolor="white">

<%

out.println("<center><img id=\"image\" src=\"loading.gif\" height=\"200\" width=\"200\"/></center>");
out.flush();

//Set BaseFolder name first
String baseFolder="webapps/webMolCS/";

//A) Get the jobid
String jobID=request.getParameter("jobid");

//B) get fingerprint
String fp=request.getParameter("fp");

//C) data which we will need for map
String CORDINATES="";
String CPDS="";
String avgCPDS="";


//C) first get the CORDINATES, CPDS and avg CPDS
baseFolder=baseFolder+"/"+jobID+"/"+fp;
BufferedReader br=new BufferedReader(new FileReader(baseFolder+"/simToQuery1.data"));

CORDINATES=br.readLine();
br.readLine();
br.readLine();
CPDS=br.readLine();
avgCPDS=br.readLine();
br.close();

//d) get colors for map
String mapFiles[]=new String []{"simToQuery1", "ActualValues", "simToQuery2", "maxSimToRef"};
String mapNames[]=new String []{"SIMtoQ1_COLOR", "ACTUALVALS_COLOR", "SIMtoQ2_COLOR", "SIMtoR_COLOR"};

for (int a=0;a<mapFiles.length;a++)
{
br=new BufferedReader(new FileReader(baseFolder+"/"+mapFiles[a]+".data"));
br.readLine();
mapNames[a]=br.readLine();
br.close();
}

String SIMtoQ1_COLOR=mapNames[0];
String ACTUALVALS_COLOR=mapNames[1];
String SIMtoQ2_COLOR=mapNames[2];
String SIMtoR_COLOR=mapNames[3];

mapNames=null;

//Print out the SMILES of CPD
br=new BufferedReader(new FileReader(baseFolder+"/DB.smi"));
String str;
String cpdsSMI="";
int noofcpds=0;
while((str=br.readLine())!=null)
{
String sarray[]=str.split(" ");
cpdsSMI=cpdsSMI+"\t"+sarray[0]+" "+sarray[1];
noofcpds++;
}
br.close();

//Get the PCs
br=new BufferedReader(new FileReader(baseFolder+"/PCs.txt"));
String pc1="PC1: "+br.readLine()+" %";
String pc2="PC2: "+br.readLine()+" %";
String pc3="PC3: "+br.readLine()+" %";
br.close();

//Read the tree connectivity
br=new BufferedReader(new FileReader(baseFolder+"/"+"tree.data"));
String idx1="";
String idx2="";

while ((str = br.readLine()) != null) {
String sarray[]=str.split(" ");
idx1=idx1+sarray[0]+";";
idx2=idx2+sarray[1]+";";
}
br.close();
%>

<div id="myModal" class="modal">
<div class="modal-content">
<div class="modal-body">
<button id="SortList" onclick="sortList()">Sort List (List-Rank)</button>
<button id="SaveList" onclick="saveToFile()">Save List</button> 
<button id="ClearList" onclick="clearList()">Clear List</button>
<button2> <b> (Click on molecule to remove it from list)</b> </button2>

<table id="mytable" style="width:100%">
</table>
</div>
</div>
</div>


<div id="wrapper">		
<div id="container"></div>

<form>
<div id="sidebar">
</form>
</div>

<script>
		var elem = document.getElementById("image");
		elem.parentNode.removeChild(elem);
</script>

	<script src="THREE.js/build/three.min.js"></script>
	<script src="THREE.js/examples/js/controls/TrackballControls.js"></script>
	<script src="THREE.js/examples/js/libs/stats.min.js"></script>
	<script src="THREE.js/examples/js/libs/dat.gui.min.js"></script>
	<script src="THREE.js/examples/js/libs/tween.min.js"></script>

	<script>

		var container, stats, controls, mouse;
		var camera, scene, renderer, particles, geometry, material, i, h, color, colors = [], sprite, size;
		var mouseX = 0, mouseY = 0;
		var windowHalfX = window.innerWidth / 2;
		var windowHalfY = window.innerHeight / 2;
		var raycaster, INTERSECTED, INTERSECTED_R, INTERSECTED_G, INTERSECTED_B;
		var simToQuery1_COLOR, simToQuery2_COLOR, simToRef_COLOR, actualVals_COLOR;
		var splitCoord, cpdsOfPixel, avgCpdsID;
		var effectController;
		var material;
		var gui;
		var x1,y1,z1;
		var mouseMoveOff=false;
		var dbc_Point, dbc_Point_R, dbc_Point_G, dbc_Point_B;
		var jobid="<%=jobID%>";
		var fp="<%=fp%>";
		var cpdsInList="";
		var AutoSelectList;
                var AutoSelectList_IDX;
                var CpdsRankAsPerFP;
		var pc1, pc2, pc3;

		//MST related variables
                var mst;
                var treePoints1;
                var treePoints2;
                var bfg;
                var mstMesh;
                var mstLineMtrl;

		//index of query compound
		var qIndex;

		//set tab name
		document.getElementById("title1").text=fp+" PCA map";
		
		init();
		animate();

		function init() {


			//###########################################################################//
			//THERE WILL BE THREE COLOR CODING FOR MAP
			//A) simToQuery1_COLOR= Here I used the compound rank provided by user.
			// Actually user do not provide a rank explicitely. I assgined the rank to compound
			// based on its position in the compound list provided by user. Here I assumed that
			// compound list provided by user is sorted according to the distance or similaritiy to query.

			//B) simToQuery2_COLOR = Here I compute the new rank for each compound in the list. 
			//So What I did is. I calculate the similarity of database compounds to query using the
			// fingerprint (selected for similarity map) and then sort the list and compute the rank for compounds. 
		
			//C) simToRef_COLOR: Here I used the similarity of database compound to the reference 
			//compounds selected for similartiy map for color coding. For each compound the maximum
			// similarity is determined.
			//###########################################################################//
	
			container = document.getElementById( "container");
			document.body.appendChild(container);

			camera = new THREE.PerspectiveCamera(70, window.innerWidth/window.innerHeight, 1, 10000);
			camera.position.set(250,300,400);
			camera.lookAt(new THREE.Vector3(250,300,400));

			scene = new THREE.Scene();
			scene.fog = new THREE.FogExp2(0x000000, 0.0009);
			sprite = THREE.ImageUtils.loadTexture("THREE.js/examples/textures/sprites/ball.png");

			//CO-ORDINATE SAME FOR ALL MAPS
			var coord = "<%=CORDINATES%>";
			splitCoord=coord.split(";");

			//CPDS OF PIXELS
			var cpds="<%=CPDS%>";
			cpdsOfPixel=cpds.split(";");

			//AVG COMPOUND OF PIXEL
			var avgcompoundids="<%=avgCPDS%>";
			avgCpdsID=avgcompoundids.split(";");
			
			//SIM to Q data
			var coordColr = "<%=SIMtoQ1_COLOR%>";
			simToQuery1_COLOR=coordColr.split(";");

			//SIM to Q data
                        var coordColr = "<%=SIMtoQ2_COLOR%>";
                        simToQuery2_COLOR=coordColr.split(";");

			//Actual values colors
			var coordColr = "<%=ACTUALVALS_COLOR%>";
                        actualVals_COLOR=coordColr.split(";");
			
			//SIM to Ref data
			var coordColr = "<%=SIMtoR_COLOR%>";
			simToRef_COLOR=coordColr.split(";");                     

			pc1="<%=pc1%>";
			pc2="<%=pc2%>";
			pc3="<%=pc3%>";

                        //Get tree points
                        var pnts1="<%=idx1%>";
                        treePoints1=pnts1.split(";");
                        var pnts2="<%=idx2%>";
                        treePoints2=pnts2.split(";");

			cog=calcCenter(splitCoord);
			
			var geometry = new THREE.BufferGeometry();
			var positions = new Float32Array(splitCoord.length * 3 );
			var colors = new Float32Array(splitCoord.length * 3);
			var sizes = new Float32Array(splitCoord.length);
			var color = new THREE.Color();
			
			for (var i = 0; i < splitCoord.length; i++ ) {
				
				var xyz=splitCoord[i].split("_");
				var col=simToQuery2_COLOR[i].split("_");

				positions[ i*3 ]     = parseFloat(xyz[0]);
				positions[ i*3 + 1 ] = parseFloat(xyz[1]);
				positions[ i*3 + 2 ] = parseFloat(xyz[2]);

				color.setRGB(parseInt(col[0])/255, parseInt(col[1])/255, parseInt(col[2])/255);
				colors[ i*3 ]     = color.r;
				colors[ i*3 + 1 ] = color.g;
				colors[ i*3 + 2 ] = color.b;
				sizes[i]  = 5;
			}

			//getIndex for query molecule
                        for(var i=0;i<cpdsOfPixel.length;i++) {
                        
                                var sarray=cpdsOfPixel[i].split("_");
                                for(var j=0;j<sarray.length;j++)
                                {

                                        var cpdData=sarray[j].split("-");
                                        if (cpdData[0]=="1")
                                        {
                                                qIndex=i;
                                                break;
                                        }
                                }       
                        }
			
			geometry.addAttribute('position', new THREE.BufferAttribute( positions, 3 ) );
			geometry.addAttribute('color', new THREE.BufferAttribute(colors, 3) );
			geometry.addAttribute( 'size', new THREE.BufferAttribute(sizes, 1) );
			//geometry.computeBoundingSphere();

			material = new THREE.PointsMaterial({size:5, map: sprite, vertexColors: THREE.VertexColors, alphaTest: 0.5, transparent: true});
			material.needsUpdate=true;
        
			particles = new THREE.Points(geometry, material);
			scene.add(particles);
			renderer = new THREE.WebGLRenderer();
			renderer.setPixelRatio(window.devicePixelRatio);
			renderer.setSize(window.innerWidth, window.innerHeight);
			container.appendChild(renderer.domElement);

			//LETS DRAW TREE======================================================
                        bfg = new THREE.BufferGeometry();
                        mstLineMtrl = new THREE.LineBasicMaterial({ vertexColors: THREE.VertexColors, color: 0xffffff, opacity: 1, linewidth: 3 });
                        var pos = [];
                        var next_positions_index = 0;
                        var colors = [];
                        var indices_array = [];
                        var counter=0;

                        for(var i=0;i<(treePoints1.length-1);i++)
                        {

                                var pntIdx1=parseInt(treePoints1[i]);
                                var pntIdx2=parseInt(treePoints2[i]);

                                var xyz1=splitCoord[pntIdx1].split("_");
                                var xyz2=splitCoord[pntIdx2].split("_");

                                var col=simToQuery2_COLOR[pntIdx1].split("_");
                                colors.push((col[0])/255, parseInt(col[1])/255, parseInt(col[2])/255);
                                colors.push((col[0])/255, parseInt(col[1])/255, parseInt(col[2])/255);

                                pos.push(xyz1[0], xyz1[1], xyz1[2]);
                                pos.push(xyz2[0], xyz2[1], xyz2[2]);
                                counter=counter+parseInt(2);

                                if (i==0)
                                {
                                indices_array.push(i, i+1);
                                }

                                else
                                {
                                indices_array.push(counter-2, counter-1);
                                }

                        }

                        bfg.setIndex( new THREE.BufferAttribute( new Uint16Array(indices_array ), 1) );
                        bfg.addAttribute('position', new THREE.BufferAttribute( new Float32Array( pos ), 3 ) );
                        bfg.addAttribute('color', new THREE.BufferAttribute( new Float32Array( colors ), 3 ) );

                        mstMesh = new THREE.LineSegments(bfg, mstLineMtrl);
                        mst = new THREE.Object3D();
                        mst.add(mstMesh);
                        scene.add(mst);
                        //=================================================================================

			//Define controls
			controls = new THREE.TrackballControls(camera, renderer.domElement);
			controls.rotateSpeed = 1.0;
			controls.zoomSpeed = 4;
			controls.panSpeed = 0.8;
			controls.noZoom = false;
			controls.noPan = true;
			controls.staticMoving = false;
			controls.dynamicDampingFactor = 0.3;
			controls.enabled=true;
			controls.target = new THREE.Vector3(cog[0], cog[1], cog[2]);
			axes = buildAxes(1000, cog[0], cog[1], cog[2]);

			raycaster = new THREE.Raycaster();
			raycaster.params.Points.threshold=1;
			mouse = new THREE.Vector2();
			renderer.sortObjects = false;
			window.addEventListener( 'mousemove', onMouseMove, false );
			window.addEventListener( 'resize', onWindowResize, false );

			//No of comppounds in the list
			var cpdsno=<%=noofcpds%>

			//format pc %
			pc1=pc1.replace("PC1: ", "");
                        pc2=pc2.replace("PC2: ", "");
                        pc3=pc3.replace("PC3: ", "");
			
			var FizzyText = function() {
                        this.PointSize=5;
                        this.MQNMaps='FP-Rank';
			this.PCs="TEST";
                        this.Axes=false;
			this.MST=true;
                        this.setCenter=setCenter;
                        this.ResetView=gui_reset;
                        this.ShowList=ShowList;
                        this.GetCompoundList=CreatList;
			this.NoofCompoundsToPick=50;
                        this.Help=Help;
			this.ExportCoord=ExportCoord;
			this.PC1=pc1+"";
			this.PC2=pc2+"";
			this.PC3=pc3+"";
                        };

                        window.onload = function() {

                        effectController = new FizzyText();
                        gui = new dat.GUI({autoPlace: true, width: 300});
                        gui.add(effectController, 'MQNMaps', ['FP-Rank','List-Rank', 'ActualVals', 'Single color']).onChange(gui_setMap).name(fp+"-PCA Map colors");

			var f1 = gui.addFolder('PC Variance %');
                        f1.add(effectController, 'PC1', pc1);
                        f1.add(effectController, 'PC2', pc2);
                        f1.add(effectController, 'PC3', pc3);


			//gui.add(effectController, 'PCs', [pc1, pc2, pc3]);
                        gui.add(effectController, 'Axes').onChange(gui_axesState);
			gui.add(effectController, 'MST').onChange(gui_MSTState);
                        gui.add(effectController, 'PointSize').min(0).max(20).step(1).name('Point size').onChange(gui_changePointSize);
                        gui.add(effectController, 'setCenter').name("Set as pivot point");
                        gui.add(effectController, 'ResetView').name("Reset view");
			gui.add(effectController, 'NoofCompoundsToPick' ).min(20).max(cpdsno).step(1).name('No. of cpds');
			gui.add(effectController, 'GetCompoundList').name("Automatic cpd list");
                        gui.add(effectController, 'ShowList');
			gui.add(effectController, 'ExportCoord');
                        gui.add(effectController, 'Help');

                        //changeDIVsideBar();

                        };

			setInterval(blink, 500);
		}
//=====================================================================================================================
		function onMouseMove( event ) {

			//event.preventDefault();
			if (mouseMoveOff)
			{
			    return;
			}

			mouse.x = (event.clientX/ window.innerWidth) * 2 - 1;
			mouse.y = - (event.clientY/ window.innerHeight) * 2 + 1;

			var geometry = particles.geometry;
			var attributes = geometry.attributes;

			raycaster.setFromCamera(mouse, camera);
			var intersects = raycaster.intersectObjects(scene.children);

			if (intersects.length > 0) {

			//Its a new point
			if (INTERSECTED!= null && INTERSECTED != intersects[0].index )
			{

			//Set the color of previous point back
			particles.geometry.attributes.color.array[INTERSECTED*3]=INTERSECTED_R;
			particles.geometry.attributes.color.array[INTERSECTED*3+1]=INTERSECTED_G;
			particles.geometry.attributes.color.array[INTERSECTED*3+2]=INTERSECTED_B;

			//get ready for new point
			INTERSECTED = intersects[0].index;
			INTERSECTED_R=particles.geometry.attributes.color.array[intersects[0].index*3];
			INTERSECTED_G=particles.geometry.attributes.color.array[intersects[0].index*3+1];
			INTERSECTED_B=particles.geometry.attributes.color.array[intersects[0].index*3+2];

			//set color for new point
			particles.geometry.attributes.color.array[intersects[0].index*3]=parseInt(1);
			particles.geometry.attributes.color.array[intersects[0].index*3+1]=parseInt(1);
			particles.geometry.attributes.color.array[intersects[0].index*3+2]=parseInt(1);
			particles.geometry.attributes.color.needsUpdate=true;

			//Get the xyz of intersected point. We will need it in other method
			x1=particles.geometry.attributes.position.array[intersects[0].index*3];
			y1=particles.geometry.attributes.position.array[intersects[0].index*3+1];
			z1=particles.geometry.attributes.position.array[intersects[0].index*3+2];
                                                
			}

			else if (INTERSECTED == null)
			{

			INTERSECTED = intersects[0].index;
			INTERSECTED_R=particles.geometry.attributes.color.array[intersects[0].index*3];
			INTERSECTED_G=particles.geometry.attributes.color.array[intersects[0].index*3+1];
			INTERSECTED_B=particles.geometry.attributes.color.array[intersects[0].index*3+2];

			particles.geometry.attributes.color.array[intersects[0].index*3]=parseInt(1);
			particles.geometry.attributes.color.array[intersects[0].index*3+1]=parseInt(1);
			particles.geometry.attributes.color.array[intersects[0].index*3+2]=parseInt(1);
			particles.geometry.attributes.color.needsUpdate=true;

			//Get the xyz of intersected point. We will need it in other method
			x1=particles.geometry.attributes.position.array[intersects[0].index*3];
			y1=particles.geometry.attributes.position.array[intersects[0].index*3+1];
			z1=particles.geometry.attributes.position.array[intersects[0].index*3+2];

			}
			}

			else
			{
			if (INTERSECTED!=null)
			{

			particles.geometry.attributes.color.array[INTERSECTED*3]=INTERSECTED_R;
			particles.geometry.attributes.color.array[INTERSECTED*3+1]=INTERSECTED_G;
			particles.geometry.attributes.color.array[INTERSECTED*3+2]=INTERSECTED_B;
			particles.geometry.attributes.color.needsUpdate=true;
			INTERSECTED=null;

			//Get the xyz of intersected point. We will need it in other method
			x1=null;
			y1=null;
			z1=null;

			}

			else
			{
			clearDataForPoint();
			document.getElementById('sidebar').style.top = '-1000px';
			document.getElementById('sidebar').style.top = '-1000px';
			}

			return;
			}

			setDataForPoint(INTERSECTED);
			
			var x = event.clientX;
                        var y = event.clientY;
                        document.getElementById('sidebar').style.top = (y+10)+'px';
                        document.getElementById('sidebar').style.left = (x+10)+'px';

		}
//===================================================================================================
		function onWindowResize() {
		      camera.aspect = window.innerWidth / window.innerHeight;
		      camera.updateProjectionMatrix();
		      renderer.setSize( window.innerWidth, window.innerHeight );
		}
//===================================================================================================
		function animate() {
		      requestAnimationFrame( animate );
		      render();
		}
//===================================================================================================
		function render() {
		      TWEEN.update();
		      controls.update();
		      renderer.render(scene, camera);
		}
//===================================================================================================
 		function stop() {

		      //If the point is interestected and mouse movemenet is on
		      if (INTERSECTED!=null && !mouseMoveOff)
		      {
		      mouseMoveOff=true;
		      if (dbc_Point==null)
		      {

		      dbc_Point=INTERSECTED;
		      dbc_Point_R=INTERSECTED_R;
		      dbc_Point_G=INTERSECTED_G;
		      dbc_Point_B=INTERSECTED_B;
		      INTERSECTED=null;
		      }

		      else
		      {
		      if (dbc_Point==INTERSECTED)
		      {
		      return;
		      }

		      //Assigned original color back
		      var geometry = particles.geometry;
		      var attributes = geometry.attributes;
		      attributes.color.array[dbc_Point*3+0]=dbc_Point_R;
		      attributes.color.array[dbc_Point*3+1]=dbc_Point_G;
		      attributes.color.array[dbc_Point*3+2]=dbc_Point_B;
		      attributes.color.needsUpdate=true;

		      dbc_Point=INTERSECTED;
		      dbc_Point_R=INTERSECTED_R;
		      dbc_Point_G=INTERSECTED_G;
		      dbc_Point_B=INTERSECTED_B;
		      INTERSECTED=null;
		      }
		      }

		      else if (mouseMoveOff)
		      {
		      mouseMoveOff=false;
		      INTERSECTED=null;
		      }
	    }
//===================================================================================================
		function moveState() {

			if (cb2.checked)
			{
			controls.staticMoving=true;
			}

			else
			{
			controls.staticMoving=false;
			}
		}
//===================================================================================================
		function rotateChange() {
			controls.rotateSpeed = document.getElementById("rotateRange").value;
		}
//===================================================================================================
		function zoomChange() {
			controls.zoomSpeed = document.getElementById("zoomRange").value;
		}
//===================================================================================================
		function changeAMB() {
			var val=document.getElementById("ambRange").value;
			amb.color.setRGB(1*val, 1*val, 1*val);
		}
//=====================================================================================================================
		function changeDIVsideBar() {
		
			var element = document.getElementById('sidebar');
			var h=element.offsetHeight;
			var w=element.offsetWidth;

			//Get the ration or factor
			var sw=screen.availWidth;
			var sh=screen.availHeight;

			var ratio=sw/sh;

			if (w >200)
			{
				var newW=(200*40)/w;
				var newH=(200*40)/h;
				newH=newH+5;
				document.getElementById('sidebar').style.width=newW+"%";
				document.getElementById('sidebar').style.height=newH+"%";
			}

			else
			{
					
			}
		}
//========================================================================================================================
		function calcCenter(t) {
    
    			var x=0;
    			var y=0;
    			var z=0;

    			for(var i=0;i<t.length;i++)
    			{
       				var vals=t[i].split("_");
       				x=parseInt(x)+parseInt(vals[0]);
       				y=parseInt(y)+parseInt(vals[1]);
       				z=parseInt(z)+parseInt(vals[2]);
    			}

			x=parseInt(x)/parseInt(t.length);
			y=parseInt(y)/parseInt(t.length);
			z=parseInt(z)/parseInt(t.length);

    			var out=[x,y,z];
    			return out;
		}
//===========================================================================================================================
		function buildAxes(length, x,y,z) {
			var axes = new THREE.Object3D();
			axes.add(buildAxis(new THREE.Vector3(0,0,0),new THREE.Vector3(length,0,0),0xFF0000,false));//+X
			axes.add(buildAxis(new THREE.Vector3(0,0,0),new THREE.Vector3(-length,0,0),0xFF0000,true));//-X
			axes.add(buildAxis(new THREE.Vector3(0,0,0),new THREE.Vector3(0,length,0),0x00FF00,false));//+Y
			axes.add(buildAxis(new THREE.Vector3(0,0,0),new THREE.Vector3(0,-length,0),0x00FF00,true));//-Y
			axes.add(buildAxis(new THREE.Vector3(0,0,0),new THREE.Vector3(0,0,length),0x0000FF,false));//+Z
			axes.add(buildAxis(new THREE.Vector3(0,0,0),new THREE.Vector3(0,0,-length),0x0000FF,true));//-Z
			axes.position.set(x,y,z);
			return axes;
		}
//===========================================================================================================================
		function buildAxis(src, dst, colorHex, dashed) {
			var geom = new THREE.Geometry(),
			mat;
			if(dashed) {
			mat = new THREE.LineDashedMaterial({ linewidth: 3, color: colorHex, dashSize: 3, gapSize: 3 });
			} else {
			mat = new THREE.LineBasicMaterial({ linewidth: 3, color: colorHex });
			}
			geom.vertices.push(src.clone());
			geom.vertices.push(dst.clone());
			geom.computeLineDistances(); // This one is SUPER important, otherwise dashed lines will appear as simple plain lines
			var axis = new THREE.Line(geom, mat, THREE.LinePieces);
			return axis;
		}
//===========================================================================================================================
		function setDataForPoint(id) {

                        //Clear Previous links
                        var sdbr = document.getElementById('sidebar');
                        while(sdbr.firstChild){
                                sdbr.removeChild(sdbr.firstChild);
                        }

                        //Set the new linksnd pictures
                        var cpdsOfPIXELS=cpdsOfPixel[id].split("_");
                        for(i=0;i<cpdsOfPIXELS.length;i++) {

                                var img = new Image(); // width, height values are optional params
                                img.src = jobid+"/mols/"+cpdsOfPIXELS[i]+".png";
                                img.id=cpdsOfPIXELS[i];
                                img.style="max-width:100%;max-height:100%;display:block";
                                document.getElementById("sidebar").appendChild(img);

                                var linkMol = document.createElement('a');
                                //var linkText = document.createTextNode(cpdsOfPIXELS[i]);
                                //linkMol.appendChild(linkText);
                                linkMol.title = cpdsOfPIXELS[i];
                                linkMol.href = "#";
                                linkMol.id=cpdsOfPIXELS[i]+"_href";
                                linkMol.innerHTML=cpdsOfPIXELS[i];
                                linkMol.style.color="#ffffff";
                                linkMol.style.display="block";
                                
                                document.getElementById("sidebar").appendChild(linkMol);

                                document.getElementById(cpdsOfPIXELS[i]+"_href").onclick=function(){

                                                if (cpdsInList=="")
                                                {
                                                        cpdsInList=this.innerHTML;
                                                }

                                                else
                                                {
                                                        cpdsInList=cpdsInList+";"+this.innerHTML;
                                                }
                                                };
                        }
		}
//===========================================================================================================================
		function clearDataForPoint() {
			
			//Clear Previous links
                        var sdbr = document.getElementById('sidebar');
                        while(sdbr.firstChild){
                                sdbr.removeChild(sdbr.firstChild);
                        }
		}	
//===================================================================================================
		function gui_setMap() {

			var x = effectController.MQNMaps;
			
			if ( x == "List-Rank")
			{
			changeMap("List-Rank", simToQuery1_COLOR);
			changeMSTcol(simToQuery1_COLOR);
			}

			if ( x == "FP-Rank")
                        {
                        changeMap("FP-Rank", simToQuery2_COLOR);
			changeMSTcol(simToQuery2_COLOR);
                        }

			if (x == "ActualVals")
			{
			changeMap("ActualVals", actualVals_COLOR);
                        changeMSTcol(actualVals_COLOR);
			}
			
			if ( x == "SIMtoRef")
			{
			changeMap("SIMtoRef", simToRef_COLOR);
			}
			
			if ( x == "Single color")
			{
			gui_setConstantColorToMap();
			}
		}
//===================================================================================================
		function changeMap(mapName, mapColor) {
			 
			var geometry = particles.geometry;
			var attributes = geometry.attributes;
			for (var i = 0; i < attributes.color.array.length/3; i++ ) {
				var col=mapColor[i].split("_");
				var r=parseInt(col[0])/255;
				var g=parseInt(col[1])/255;
				var b=parseInt(col[2])/255;
				attributes.color.array[i*3+0]=r;
				attributes.color.array[i*3+1]=g;
				attributes.color.array[i*3+2]=b;
			}

			particles.geometry.attributes.color.needsUpdate=true;
			dbc_Point=null;
		}
//===================================================================================================
                function changeMSTcol(clr) {

                        var colors=[];
                        for(var i=0;i<(treePoints1.length-1);i++)
                        {
                                var pntIdx1=parseInt(treePoints1[i]);
                                var pntIdx2=parseInt(treePoints2[i]);

                                var col=clr[pntIdx1].split("_");
                                colors.push((col[0])/255, parseInt(col[1])/255, parseInt(col[2])/255);
                                colors.push((col[0])/255, parseInt(col[1])/255, parseInt(col[2])/255);
                        }

			if (effectController.MST)
                        {
                                scene.remove(mst);
                                bfg.addAttribute('color', new THREE.BufferAttribute(new Float32Array(colors), 3));
                                mstMesh = new THREE.LineSegments(bfg, mstLineMtrl);
                                mst = new THREE.Object3D();
                                mst.add(mstMesh);
                                scene.add(mst);
                        }

                        else
                        {
                                bfg.addAttribute('color', new THREE.BufferAttribute(new Float32Array(colors), 3));
                                mstMesh = new THREE.LineSegments(bfg, mstLineMtrl);
                                mst = new THREE.Object3D();
                                mst.add(mstMesh);
                        }
                }
//===================================================================================================
		function gui_axesState() {

			if (effectController.Axes)
			{
			scene.add(axes);
			}

			else
			{
			scene.remove(axes);
			}

		}
//===================================================================================================
                        function gui_MSTState() {

                        if (effectController.MST)
                        {
                        scene.add(mst);
                        }

                        else
                        {
                        scene.remove(mst);
                        }
                }
//===================================================================================================
		function gui_HideDB() {

			if (effectController.HideDB)
			{

			    for(var i=0;i<cpdsOfPixel.length;i++) {

				if (cpdsOfPixel[i].lastIndexOf("Ext", 0)==-1)
				{
				particles.geometry.attributes.position.array[ i*3 ]     = parseFloat(3000);
				particles.geometry.attributes.position.array[ i*3 + 1 ] = parseFloat(3000);
				particles.geometry.attributes.position.array[ i*3 + 2 ] = parseFloat(3000);
				}
			    }	
			}

			else
			{
			    
			var coord = "<%=CORDINATES%>";
			var splitCoord=coord.split(";");
				
			    for(var i=0;i<splitCoord.length;i++) {
				var xyz=splitCoord[i].split("_");
				particles.geometry.attributes.position.array[ i*3 ]     = parseFloat(xyz[0]);
				particles.geometry.attributes.position.array[ i*3 + 1 ] = parseFloat(xyz[1]);
				particles.geometry.attributes.position.array[ i*3 + 2 ] = parseFloat(xyz[2]);
			    }

			}
			particles.geometry.attributes.position.needsUpdate=true;
		}
//===================================================================================================
		function gui_changePointSize() {
			var s=parseInt(effectController.PointSize);
			material.size=s;
			render();
		}
//===================================================================================================
		function gui_setConstantColorToMap() {
			
			var s=effectController.MapColor;
			var clr = new THREE.Color(s);
			var geometry = particles.geometry;
			var attributes = geometry.attributes;

			for (var i = 0; i < attributes.color.array.length/3; i++ ) {
			    attributes.color.array[i*3+0]=clr.r;
			    attributes.color.array[i*3+1]=clr.g;
			    attributes.color.array[i*3+2]=clr.b;
			}

			particles.geometry.attributes.color.needsUpdate=true;
			dbc_Point=null;
			effectController.MQNMaps="Single color";

			for (var i in gui.__controllers) {
			    gui.__controllers[i].updateDisplay();
  			}
		}
//===================================================================================================
		function gui_setConstantColorToExt() {
				
			var s=effectController.ExtColor;
			var clr = new THREE.Color(s);
			var geometry = particles.geometry;
			var attributes = geometry.attributes;
			
			for (var i = 0; i <cpdsOfPixel.length; i++ ) {

			    if (cpdsOfPixel[i].search("Ext-") != -1)
			    {
				attributes.color.array[i*3+0]=clr.r;
				attributes.color.array[i*3+1]=clr.g;
				attributes.color.array[i*3+2]=clr.b;
			    }
			}
			particles.geometry.attributes.color.needsUpdate=true;
		}
//===================================================================================================
		function setCenter() {
		
			if (!mouseMoveOff)
			{
			alert("*******************************\n\nFirst lock a point by mouse double click.\n\n1st time mouse double click on a point: point get selected and locked\n\n2nd time mouse double click on a point or anywhere on screen: point get unlock (but remain selected or marked)\n\n*******************************");
			return;
			}

			if (dbc_Point==null)
			{
			alert("*******************************\n\nFirst lock a point by mouse double click.\n\n1st time mouse double click on a point: point get selected and locked\n\n2nd time mouse double click on a point or anywhere on screen: point get unlock (but remain selected or marked)\n\n*******************************");
			return;
			}
				
			controls.enabled=false;
			new TWEEN.Tween(controls.target).to( {
			    x: x1,
			    y: y1,
			    z: z1 }, 2000)
			    //.easing( TWEEN.Easing.Elastic.Out).start();
			    .easing(TWEEN.Easing.Linear.None).start();
			    controls.enabled=true;
		}
//===================================================================================================
		function gui_reset() {

			controls.reset();
			controls.target = new THREE.Vector3(cog[0], cog[1], cog[2]);
			controls.rotateSpeed=2;
			controls.zoomSpeed=4;
			controls.staticMoving=false;

			//Set the point size
			effectController.PointSize=5;
			gui_changePointSize();
			for (var i in gui.__controllers) {
			    gui.__controllers[i].updateDisplay();
			}
		}
//===================================================================================================
		function connectToBrowser() {
		
			if (dbc_Point!=null)
			{
			var dbID=cpdsOfPixel[dbc_Point].split("_")[0]
			var link = "http://dcb-reymond23.unibe.ch:8080/DrugBank.MFB/index.html?" + dbID;
			window.open(link);
			}

			else
			{
			var link = "http://dcb-reymond23.unibe.ch:8080/DrugBank.MFB/index.html";
			window.open(link);
			}
		}
//===================================================================================================
		                function Help() {

				window.open("help.html", '_blank');
				return;

                        alert("A) Map colors: colors are based on rank of compounds in sorted database and actual values provided by user in input.\n\n"+

                              "B) FP-Rank: compounds were ranked according to decreasing similarity to query using this fingerprint (FP).\n\n" +

                              "C) List-Rank: Compounds were ranked based on their appearence in the list provided by the user.\n\n" +

                              "D) ActualVals: Compounds were color coded according to the actual values provided by user in input (third column).\n\n" +

                              "E) PCs: % variance covered by principal component 1, 2 and 3 (PC1-3).\n\n" +

                              "F) Axes: show or hide axes\n\n" +

                              "G) MST: show or hide minmum spanning tree\n\n" +

                              "H) Point size: increase or decrease size of points.\n\n" +

                              "I) Set as pivot point: rotation and camera lookup point set to the selected (locked) point in 3D-map.\n\n"  +

                              "J) Reset view: restore the map to starting position and reset the point size.\n\n"+

                              "K) No. of cpds: set the number of compounds for automatic selection.\n\n"+

                              "L) Automatic cpd list: automatically select the compounds from database.\n\n" +

                              "M) ShowList : shows the list of compounds selected by user.\n\n" +

                              "N) Add the compound to selection list: first lock the point by mouse double click, then click on molecule ID to put it in the selection list.\n\n" +
                             "O) ExportCoord: export the co-ordinates, smiles string and IDs for all the compounds." +

                                "\n\n==Additional Functions==\n" +

                                "* Select or lock point: mouse double click\n" +

                                "* Unselect or unlock point: mouse double click\n" +

                                "* Zoom: mouse middle scroll\n" +

                                "* Rotation: press mouse left button and dragg\n" +

                                "* Lock mouse over: double click any where"
)
                }
//===================================================================================================
		function ShowList() {

			//Create table first
			showTable();

			var modal = document.getElementById('myModal');
			modal.style.display = "block";
		}

		window.onclick = function(event) {

			var modal = document.getElementById('myModal');
    			if (event.target == modal) {
        		modal.style.display = "none";
    			}
		}
//===================================================================================================
//THIS METHOD 
		function showTable() {

			//Remove previous table
			var myTable = document.getElementById("mytable");
			var rowCount = myTable.rows.length;
			for (var x=rowCount-1; x>-1; x--) {
				myTable.deleteRow(x);
			}
			
			if (cpdsInList=="")
			{
				return;
			}

			//Make uniq
			cpdsInList=makeUniq(cpdsInList);
			var selectedCpds=cpdsInList.split(";");
			var noOfMols=selectedCpds.length;	

			//Clear and make new table
			for(var i=0;i<selectedCpds.length;i++) {

			var table = document.getElementById('mytable');
			var newRow1   = table.insertRow(table.rows.length);
			var newRow2   = table.insertRow(table.rows.length);

			var toGoIDX=i+4;

			if (toGoIDX>noOfMols)
			{
				toGoIDX=noOfMols;
			}

			var control=0;
			for(var j=i;j<toGoIDX;j++)
			{

				var cpdID= selectedCpds[j];

				//First is Image
				//var img  = newRow1.insertCell(control);
				var img = document.createElement('img');
				img.src = jobid+"/mols/"+cpdID+".png";
				var newCell  = newRow1.insertCell(control);
				newCell.style.backgroundColor = "black";
				newCell.style.color = "white";
				newCell.style.borderColor = "white";
				newCell.style.borderStyle="solid";
				newCell.align="center";
				newCell.appendChild(img);
				img.id=cpdID;
                                img.onclick = function() {removeFromSelectionList(this);};

				var newCell  = newRow2.insertCell(control);
				var newText  = document.createTextNode(cpdID);
				newCell.style.backgroundColor = "black";
				newCell.align="center";
				newCell.style.color = "white";
				newCell.style.borderColor = "white";
				newCell.style.borderStyle="solid";
				newCell.appendChild(newText);

				control=control+1;
				i=j;

			}
			}
		}
//===================================================================================================
		function removeFromSelectionList(elem) {

				var selectedCpds=cpdsInList.split(";");
                                var out="";
                                for(var i=0;i<selectedCpds.length;i++)
                                {
                                        if (selectedCpds[i]!=elem.id)
                                        {
                                                if (out=="")
                                                {
                                                        out=selectedCpds[i];
                                                }
        
                                                else
                                                {
                                                        out=out+";"+selectedCpds[i];
                                                }
                                        }

                                        else
                                        {
                                        }
                                }

                                cpdsInList=out; 
                                showTable();
		}
//===================================================================================================
		function makeUniq(data) {

				var allSelected=data.split(";");
				var selectedUniq = [];
				selectedUniq.push(allSelected[0]);

				for(var i=0;i<allSelected.length;i++) {

					var toAdd="YES"
					for(var j=0;j<selectedUniq.length;j++) {

						if (allSelected[i]==selectedUniq[j])
						{
							toAdd="NO";
						}

					}

					if (toAdd=="YES")
					{
						selectedUniq.push(allSelected[i]);
					}
				}
				
				var out;
				for(var i=0;i<selectedUniq.length;i++)
				{

					if (i==0)
					{
						out=selectedUniq[i];
					}
					else
					{
					out=out+";"+selectedUniq[i];
					}
				}
			
				return out;	
		}
//===================================================================================================
		function clearList() {

			cpdsInList="";
                        var myTable = document.getElementById("mytable");
                        var rowCount = myTable.rows.length;
                        for (var x=rowCount-1; x>-1; x--) {
                                myTable.deleteRow(x);
                        }
		}
//===================================================================================================
		function saveToFile() {
	
			if (cpdsInList=="")
			{
				alert("Compound list is empty: Please select some molecules first!")
				return;
			}
	
			//Get all the cpds SMI
			var ssmi="<%=cpdsSMI%>";
			var smiList=ssmi.split("\t");

			//Get the selected cpds
                        var cpdsArray=cpdsInList.split(";");

                        //Get the smiles for selected cpds
                        var cpdsToWrite="";

                        for(var i=0;i<cpdsArray.length;i++)
                        {
                                for(var j=0;j<smiList.length;j++)
                                {
                                        var cpd=smiList[j].split(" ");
                                        if (cpd[1]==cpdsArray[i])
                                        {
                                                cpdsToWrite=cpdsToWrite+smiList[j]+"\n";
                                        }
                                }
                        }
        
                        var blob = new Blob([cpdsToWrite], {type: "text/plain;charset=utf-8"});
                        saveAs(blob, "selectionList.txt");
		}
//===================================================================================================
                function ExportCoord() {

                        var coordtoWrite="X_Y_Z"+"\t"+"SMILES"+"\t"+"CompoundID";
                        var ssmi="<%=cpdsSMI%>";
                        var smiList=ssmi.split("\t");

                        //Create hash map for javascript
                        var smiMap = {};
                        for(var i=0;i<smiList.length;i++)
                        {
                                var data=smiList[i].split(" ");
                                smiMap[data[1]]=data[0];
                        }

                        for (var i=0;i<splitCoord.length;i++)
                        {

                                var cpdsArray=cpdsOfPixel[i].split("_");
                                for(var j=0;j<cpdsArray.length;j++)
                                {
                                        var s=smiMap[cpdsArray[j]];
                                        coordtoWrite=coordtoWrite+"\n"+splitCoord[i]+"\t"+s+"\t"+cpdsArray[j];
                                }
                        }

                        var blob = new Blob([coordtoWrite], {type: "text/plain;charset=utf-8"});
                        saveAs(blob, "coordinates.txt");
                }
//===================================================================================================
		function CreatList() {

			AutoSelectList=[];
			AutoSelectList_IDX=[];
			var noofcpdsToCollect=effectController.NoofCompoundsToPick;
			var infile = jobid+"/"+fp+"/CpdRankAsperFP.txt";
			var url = infile;
                        var file = new XMLHttpRequest();
                        file.open("GET",url,true);
                        file.send();

                        file.onreadystatechange = function(){
                                
                        if (file.readyState== 4 && file.status == 200){
                        	
				CpdsRankAsPerFP=file.responseText;
				var list=CpdsRankAsPerFP.split("\n");
				var listSize=list.length-1;	
				if (listSize<noofcpdsToCollect)	
				{
					noofcpdsToCollect=listSize;
				}

				var rank=[];
        			var rankSQRT=[];
				var maxsqrt;
				for (var a = 1; a <=listSize; a++) {
				var sqr=Math.sqrt(a);
            			rankSQRT.push(sqr);
            			rank.push(a);
				maxsqrt=sqr;
        			}
				
				var step = maxsqrt / noofcpdsToCollect;
				for (var a = 1; a <= maxsqrt; a = a + step) {

            			var winnerDist = 1000000;
            			var winnerRank = 1;

            			for (var b = 0; b < rankSQRT.length; b++) {
                			var abs = Math.abs(a - rankSQRT[b]);
                			if (abs < winnerDist) {
                    				winnerDist = abs;
                    				winnerRank = rank[b];
                			}
            			}
           
				var cpdID=list[winnerRank-1].split(" ")[1];
				AutoSelectList.push(cpdID);	
        			}

				for(var i=0;i<AutoSelectList.length;i++) {

					var cpd=AutoSelectList[i];
					var idx;

					for(var j=0;j<cpdsOfPixel.length;j++)
					{
						var cpdsOfpxl=cpdsOfPixel[j].split("_");
						for(var k=0;k<cpdsOfpxl.length;k++)
						{
							if (cpd==cpdsOfpxl[k])
							{
								idx=j;
							}
						}
					}

					AutoSelectList_IDX.push(idx);
				}	

				//Now create the table
				for(var i=0;i<AutoSelectList.length;i++)
                        	{
                                	if (cpdsInList=="")
                                	{
                                	cpdsInList=AutoSelectList[i];
                                	}
                                
                                	else
                                	{
                                	cpdsInList=cpdsInList+";"+AutoSelectList[i];
                                	}
                        	}

                        	alert(AutoSelectList.length+" Compounds Successfully Selected. "+ "Selected points are marked in white!");
				setColorToSelectedCpds();
	
                        }
                        }
		}
//===================================================================================================
		function sortList() {

			if (cpdsInList=="")
			{
				return;
			}

			var cpdList=cpdsInList.split(";");
			var completArray1=[];

			for (var i=0;i<5000;i++)
                        {
                                completArray1.push("TEST");
                        }

			for(var i=0;i<cpdList.length;i++)
                        {
                                var rank=cpdList[i].split("-")[0]
                                completArray1[rank]=cpdList[i];
                        }

			var sortedCpdsIDs=[];
			for(var i=0;i<completArray1.length;i++)
                        {

                                if (completArray1[i]!="TEST")
                                {
                                        sortedCpdsIDs.push(completArray1[i]);
                                }
                        }

			cpdsInList="";
			for(var i=0;i<sortedCpdsIDs.length;i++)
                        {
                                if (cpdsInList=="")
                                {
                                cpdsInList=sortedCpdsIDs[i];
                                }
                                
                                else
                                {
                                cpdsInList=cpdsInList+";"+sortedCpdsIDs[i];
                                }
                        }

			showTable();
		}
//===================================================================================================
		function setColorToSelectedCpds() {

			var geometry = particles.geometry;
                        var attributes = geometry.attributes;
                        for (var i = 0; i < AutoSelectList_IDX.length; i++ ) {
                                attributes.color.array[AutoSelectList_IDX[i]*3+0]=1;
                                attributes.color.array[AutoSelectList_IDX[i]*3+1]=1;
                                attributes.color.array[AutoSelectList_IDX[i]*3+2]=1;
                        }

                        particles.geometry.attributes.color.needsUpdate=true;
                        dbc_Point=null;

		}

//===================================================================================================
                var cntrl=0;
                function blink() {

                        if (cntrl==0)
                        {
                        cntrl=1;
                        particles.geometry.attributes.color.array[qIndex*3]=parseInt(1);
                        particles.geometry.attributes.color.array[qIndex*3+1]=parseInt(1);
                        particles.geometry.attributes.color.array[qIndex*3+2]=parseInt(1);
                        particles.geometry.attributes.color.needsUpdate=true;
                        }

                        else

                        {
                        cntrl=0;
                        particles.geometry.attributes.color.array[qIndex*3]=parseInt(1);
                        particles.geometry.attributes.color.array[qIndex*3+1]=parseInt(0);
                        particles.geometry.attributes.color.array[qIndex*3+2]=parseInt(0);
                        particles.geometry.attributes.color.needsUpdate=true;
                        }

                }
//===================================================================================================
</script>
</body>
</html>
