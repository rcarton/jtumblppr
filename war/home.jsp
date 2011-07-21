<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE HTML>

<html>
<head>
	
	<!--link href='http://fonts.googleapis.com/css?family=Droid+Sans&v1' rel='stylesheet' type='text/css'-->
	<link href='http://fonts.googleapis.com/css?family=Artifika&v2' rel='stylesheet' type='text/css'>
	
	<link rel="stylesheet" href="css/ppr.css" type="text/css">
	<link rel="stylesheet" href="css/colors.css" type="text/css">
	<script language="Javascript" type="text/javascript" src="js/jquery-1.6.1.min.js"></script> 
	<script language="Javascript" type="text/javascript" src="js/autocenter.js"></script> 
	<script language="Javascript" type="text/javascript" src="js/urlencode.js"></script> 
	<script language="Javascript" type="text/javascript" src="js/tumblppr.js"></script> 
	
	<title>MK A WLLPPR FRM A TUMBLR.</title>
	
</head>


<body>
<div id="loading"></div>
<div id="download"></div>
<div id="mask"></div>

<div id="left">
	
	<div id="logo"> </div>
	
	<div id="left-content">
		<h1>Settings <div id="settings-wheel"> </div></h1>
		<div id="setting">
			http:<span style="font-family: 'Arial'; padding-right: 5px;">//</span><input id="select-tumblr-input" type="text" value="fysurf.com" />
		</div>
		
		<div class="setting">fit to width<div id="option-fit-width" class="setting-right toggle"> </div></div>
		<div class="setting">dimensions<div class="setting-right"><input id="dim-width" type="text" value="1680" /> x <input id="dim-height" type="text" value="1050" /></div></div>
		<!--<div class="setting">wrap bottom/up<div class="setting-right"></div></div>
		<div class="setting">color<div class="setting-right"></div></div>
		<div class="setting">order<div class="setting-right"></div></div>	-->
		<div class="button" id="button-preview">PREVIEW &gt;</div>
		
		<h1>Popular</h1>
	</div>
	
	
	

	
</div>

<div id="right">
	<div id="right-content">
		<h1>Preview <span style="font-size: 0.5em;">(1:2)</span></h1>
		<div id="preview-wrapper">
			<canvas id="preview-canvas"></canvas>
			<div id="click-to-download"></div>
		</div>
		<canvas id="fullsize-canvas"></canvas>
		
	</div>
</div>

</body>



</html>