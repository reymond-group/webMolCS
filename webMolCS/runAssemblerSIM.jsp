<%@ page import="java.io.File"%>
<%@ page import="java.io.BufferedWriter" %>
<%@ page import="java.io.FileWriter" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.FileReader" %>

<head>
		<link rel="shortcut icon"  href="1.ico"/>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
                
                <title>SIMmap</title>
		<style>

			html, body {
				height: 100%;
			}

			body {
				background-color: #000000;
				margin: 0;
				font-family: Arial;
				#overflow: hidden;
			}

			a {
				color: #ffffff;
			}

			#info {
				top: 10px;
				position: absolute;
				width: 100%;
				color: #ffffff;
				padding: 5px;
				font-family: Arial;
				font-size: 15px;
				font-weight: bold;
				text-align: center;
				z-index: 1;
			}

			#link {
				width: 100%;
				color: #ffffff;
				padding: 5px;
				font-family: Arial;
				font-size: 15px;
				text-align: center;
				z-index: 1;
			}

			#jobstatus {
                                width: 100%;
				color: #ffffff;
                        	padding: 5px;
                               	font-family: Arial;
                                font-size: 15px;
				font-weight: bold;
                               	text-align: center;
                               	z-index: 1;
			}

			#jobinfo1 {
				width: 100%;
                               	color: #ff0000;
	                       	padding: 5px;
                               	font-family: Arial;
                               	font-size: 12px;
                               	font-weight: bold;
                               	text-align: center;
                               	z-index: 1;
			}

			button {
                             	color: rgba(127,255,255,0.75);
                               	background:grey;
                               	outline: 0px solid rgba(127,255,255,0.75);
                              	border: solid 2px;
				border-radius:5px;
                               	padding: 5px 10px;
                               	cursor: pointer;
                               	color: #ffffff;
                               	font-size: 12px;
				font-family: Arial;
			}
		</style>
<%
//===================================================================

//Get the Molecules
String molecules=request.getParameter("mols");

//Get the fp
String fp=request.getParameter("fp");

//get the fp decription
String fpd=request.getParameter("fpd");

//Get Time stamp (this will be job id)
long start = System.currentTimeMillis();

//Massage to display
String msg="";

//This will be a link to the user job
String link="localhost:8080/webMolCS/yourSIM.html?jobID="+start+"&fp="+fp;

//get IP and date and write to log
String date = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
String tmpip = request.getRemoteAddr();
BufferedWriter bwlog=new BufferedWriter(new FileWriter("webMolCS.log", true));
bwlog.write(date+" "+tmpip+" "+fp+" "+start+"\n");
bwlog.close();
//===================================================================

//Write molecules to file
String mlist[]=molecules.split("\n");

BufferedWriter bw=new BufferedWriter(new FileWriter("webapps/webMolCS/"+start+".txt"));
for(int a=0;a<mlist.length;a++)
{
bw.write(mlist[a]+"\n");
}
bw.close();

//===================================================================

//boolean determines if the job can be run or not
boolean canRun=false;

//IP address of user
String ip = request.getRemoteAddr();

//if mols are greater than 20 and less than 5000, then only run job
if (mlist.length>=20 && mlist.length<=40000)
{

File fl = new File ("webapps/webMolCS/IPQue/"+ip);

//if the log file for user exists, check if the previous job from user finished or not!
//if the previous job failed (ERROR) or completed ("JOB FINISHED"), then only submit new one!
if (fl.exists())
{
	BufferedReader br = new BufferedReader(new FileReader(fl));
	String str;

	while ((str = br.readLine()) != null) {

		if (str.contains("ERROR")) {
			fl.delete();
			fl.createNewFile();
			canRun=true;
		}

		if (str.contains("JOB FINISHED")) {
			fl.delete();
			fl.createNewFile();
			canRun=true;
		}

	}
	br.close();

	if (!canRun) {
		msg="SORRY! CAN NOT SUBMIT JOB. YOUR PREVIOUS JOB IS ALREADY RUNNING!";
		link="";
	}
}

else
{
	fl.createNewFile();
	canRun=true;
}
}

//If mols are less than 20 or more than 5000, do not submit
else
{
msg="SORRY! CAN NOT SUBMIT JOB. INPUT MINIMUM 20 and MAXIMUM 5000 MOLECULES.";
link="";
}

//Check if the job can be run, if yes, then submit
if (canRun)
{
//Start process
ProcessBuilder pb = new ProcessBuilder("/bin/bash", "masterSIM.sh", start+"", ip, fp);
String cwd = System.getProperty("user.dir");
pb.directory(new File("/usr/local/tomcat/webapps/webMolCS"));
pb.start();
msg="YOUR JOB IS SUBMITTED. YOU CAN BOOKMARK BELOW LINK TO ACCESS YOUR JOB IN FUTURE!"; 
}
//===================================================================
%>

		<body>
		<div id="info">
		<script>
			var msg = "<%=msg%>";
                	document.getElementById("info").innerHTML=msg;
		</script>

		<div id="link">
		<a href="" target="_blank" id="url"></a>
		</div>

		<script>
                var msg = "<%=link%>";
                document.getElementById("url").innerHTML=msg;
                document.getElementById("url").href=msg;
        	</script>

		<hr width="70%"></hr>

		<h2>Similarity PCA Chemical Space</h2>

        	<form name="theform" method="post" action="visualizerSIM.jsp" target="_blank">
        	<button type="submit" id="FPbtn" disabled></button><br>
        	<input type="hidden" name="fp" id="fp" value="">
        	<input type="hidden" name="jobid" id="jobid" value="">
		<script>

         	var msg1 = "<%=msg%>";
         	var msg2 = "<%=link%>";
         	var fingerprint="<%=fp%>";
         	var fingerprintD="<%=fpd%>";
         	var JOBid="<%=start%>";

         	document.getElementById("jobid").value=JOBid;
         	document.getElementById("FPbtn").innerHTML=fingerprintD;
         	document.getElementById("fp").value=fingerprint;

		</script>
        	</form>

        	<hr width="70%"></hr>

        	<div id="jobstatus">
        	Job Status
        	</div>

        	<div id="jobinfo1">
        	</div>

		</div>

         	<script>

		//=========================================================================
		//Get the log file for this job nd update innerhtml
          	function loadDoc() {
                        
			var fl= document.theform.jobid.value;
			var xhttp = new XMLHttpRequest();
			xhttp.onreadystatechange = function() {

			if (xhttp.readyState == 4 && xhttp.status == 200) {
			document.getElementById("jobinfo1").innerHTML = xhttp.responseText;
			enabledButtons();
                        }

			else
			{
			document.getElementById("jobinfo1").innerHTML = "Your JOB DOESN'T EXIST";
			}
			};

			xhttp.open("GET", "LOGs/"+fl, true);
			xhttp.send();
          	}

		//=========================================================================
		//Enabled the buttons on completing the job
		function enabledButtons() {

			var data=document.getElementById("jobinfo1").innerHTML.split("\n");
			for(var i=0;i<data.length;i++)
			{

				//====================================
				var a2=data[i].indexOf("COMPLETED");

				if (a2!=-1)
				{
				document.getElementById("FPbtn").disabled = false;
				document.getElementById("FPbtn").style.background="rgb(27, 49, 13)";
				}
				//======================================
			}
                }
		//=========================================================================
		//Check the log file after every 5seconds
		setInterval(function() {
			loadDoc();
			enabledButtons();
		}, 5000);
		//=========================================================================
                </script>
</body>
