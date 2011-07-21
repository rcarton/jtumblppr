
/**
 * GetUrlParameters
 * http://www.netlobo.com/url_query_string_javascript.html
 * @param name
 * @returns
 */
function gup(name) {
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}

/**
 * Displays the loading popup, displays "Loading" if no message is set
 * @param message
 */
function loading(message) {
	if (message == undefined) { message = "Loading"; }
	var l = $("#loading");
	l.html(message);
	l.center({inside: "#preview-wrapper", vertical: false});
	l.show();
}
function stopLoading() {
	$("#loading").hide();
}
function go() {
	var url = $("#select-tumblr-input").val();
	
	resetCanvas(preview_canvas);
	var data = {
			tumblr_url: url
			}
	
	loading("Loading preview images from tumblr...");
	
	$.ajax({
		  url: "/ppr",
		  type: "GET",
		  data: data,
		  success: function(result) {
			  eval(result);	//tumblr_api_read
			  
			  if(tumblr_api_read["error"] != undefined) {
				  alert(tumblr_api_read["error"]);
				  stopLoading();
			  } else {
				  loadCanvas(tumblr_api_read, preview_canvas);
				  stopLoading();
			  }
			  
		  },
		  error: function(err) {
			  alert("There was an error, but this is not a helpful message. (yet)");
			  stopLoading();
		  } 
		});
	
}

var posts = [];	//JSON posts

function loadCanvas(t, canvas) {
	// Get the list of photo posts
	var debug = ""; 
	
	posts = [];
	
	for (var i=0; i<t["posts"].length; i++) {
		var post = t["posts"][i];
		
		if (post["type"] == "photo") {
			posts.push(post);
		}
	}
	
	if (posts.length == 0) {
		alert("no photo post found!");
		return;
	}
	
	drawCanvas(posts, canvas);
}


function drawCanvas(p, canvas) {
	var method = 'column';
	
	if (method == 'column') drawCanvas_column(p, canvas);
	
}


// Globals for drawNextImage
var post_number;
var col_height;
var current_col;
var ncol;
var col_w;
var photo_url;

/**
 * Draws images on the canvas column by column
 * @param p
 */
function drawCanvas_column(posts, canvas) {

	var size = 500;
	
	// Preview: size = 250
	if (canvas.id == "preview-canvas") { size = size/2 };
	loadImages(posts, size, canvas, drawCanvas_column_callback);
}

function drawCanvas_column_callback(size, canvas) {
	var context = canvas.getContext("2d");
	
	// Options
	var options = {};
	options["fitToWidth"] = !$("#option-fit-width").hasClass("off");
	
	// Init
	var col_w = size;
	var current_col = 0;
	var init_col = -1;
	var ncol = Math.ceil(canvas.width/col_w);
	var ratio = 1;
	
	var images;
	if (canvas.id == 'preview-canvas') { images = preview_images; }
	else { images = fullsize_images; }
	
	// Find the biggest layout fitting width with the given image size (only downsizing)
	if (options.fitToWidth) {
		var r = canvas.width % col_w;
		if (r == 0) { ratio = 1; }
		else {
			//ncol = 1;
			col_w = Math.round(canvas.width / ncol);
			ratio = col_w/size;
		}
	}
	
	// Column height
	var col_height = [];
	for (var i=0; i<10; i++) col_height[i] = 0;
	
	var finished = 0;
	while(!finished) {
		finished = 1;
		for (var i=0; i< col_height.length; i++) { finished &= (col_height[i] > canvas.height); }
		
		image = 0;
		// Try to draw as many images as possible on the canvas
		for (image in images) {
			// Find next available column
			init_col = -1;
			while(col_height[current_col] >= canvas.height && current_col != init_col) {
				// Initialize first column
				if (init_col == -1) init_col = current_col;
				// Retry with next column
				current_col = (current_col +1) % ncol;
			}
				
			// No room left on the canvas!
			if (init_col == current_col) { postDraw(canvas); return};
				
			// Draw
			context.drawImage(images[image], current_col*col_w, col_height[current_col], size*ratio, images[image].height*ratio);
			col_height[current_col] += images[image].height*ratio;
		}
	}
	
	postDraw(canvas);
}

function postDraw(canvas) {
	if (canvas.id == "fullsize-canvas") {
		stopLoading();
		$("#download").html("<img src="+fullsize_canvas.toDataURL("image/jpeg")+" />");
		$("#download").fadeIn('fast');
		centerDownload();
	} else {
		$("#click-to-download").fadeIn('fast');
		$(canvas).addClass("download");
	}
}

/**
 * Centers the download div
 */
function centerDownload() {
	var w = $(window);
	var d = $("#download");
	var props = {position: 'absolute'};
	var width = $("#dim-width").val();
	var height = $("#dim-height").val();
	
	var top = (w.height() - height)/2;
	var left = (w.width() - width)/2;
	
	$.extend(props, {top: top+'px'});
	$.extend(props, {left: left+'px'});
	d.css(props);
}

var preview_images = {};
var fullsize_images = {};
/**
 * Loads all the images at once
 * then call the callback method
 */
function loadImages(posts, size, canvas, callback) {
	var loadedImages = 0;
	var numImages = posts.length;
	var photo_url = "photo-url-" + size;
	var fullsize_url = "/img?id="
	
	var images;
	if (canvas.id == 'preview-canvas') { images = preview_images; }
	else { images = fullsize_images; }
		
		
	for (var p in posts) {
		if (p > 40) break;
		
	    images[p] = new Image();
	    images[p].onload = function(){
	        if (++loadedImages >= numImages) {
	            callback(size, canvas);
	        }
	    };
	    
	    if (canvas.id == 'preview-canvas') {
	    	images[p].src = posts[p][photo_url];
	    } else {
	    	images[p].src = fullsize_url + posts[p]["id"] + "&url="+posts[p][photo_url];
	    }
	}
}


function resetCanvas(canvas) {

	// Dimensions
	var width = $("#dim-width").val();
	var height = $("#dim-height").val();
	
	if (canvas.id == "preview-canvas") {
		canvas.width = Math.round(width/2);
		canvas.height = Math.round(height/2);
		$(canvas).removeClass("download");
		$("#preview-wrapper").width($("#preview-canvas").width() + $("#preview-wrapper").css('padding')*2);
	} else {
		canvas.width = width;
		canvas.height = height;
	}
}

function displayDownload() {
	resetCanvas(fullsize_canvas);
	//$("#fullsize-canvas").show();
	loading("Generating image...");
	
	var post_list = [];
	for (var p in posts) {
		post_list.push({id: posts[p]["id"], url: posts[p]["photo-url-500"]});
	}
	
	var data = JSON.stringify( post_list);
	
	drawCanvas(posts, fullsize_canvas);
	
	/*
	$.ajax({
		  url: "/storeImages",
		  type: "POST",
		  data: {post_list: data},
		  success: function(result) {
				  drawCanvas(posts, fullsize_canvas);
				  stopLoading();
		  },
		  error: function(err) {
			  alert("There was an error, but this is not a helpful message. (yet)");
			  stopLoading();
		  } 
		});
		*/
}


$(document).ready(function() {
	
	// Get the canvas 
	preview_canvas = document.getElementById("preview-canvas");
    fullsize_canvas = document.getElementById("fullsize-canvas");
    
    // Auto resolution
    $("#dim-height").val(window.screen.height);
    $("#dim-width").val(window.screen.width);
    
    // Reset canvas
    resetCanvas(preview_canvas);
    resetCanvas(fullsize_canvas);
    
    // Add the link on the logo
    $("#logo").click(function(){ window.location = "/"; });
    
    // Toggles
    $(".toggle").click(function(){ $(this).toggleClass("off"); });
    
    // Buttons
    $("#button-preview").click(function(){ go(); });
    
    // Bind enter to the input
    $('#select-tumblr-input').bind('keyup', function(e){
    							if ((e.keyCode || e.which) == 13) { $("#button-preview").click(); } 
    						});
    
    $("#preview-canvas").click(function(){ if ($(this).hasClass("download")) { displayDownload(); } });
    
    // Download popup
    $("#download").click(function(){$(this).hide();});
    
    // Look for an url in parameter
    var requestedTumblr = gup("u");
	if (requestedTumblr != "") {
		$("#select-tumblr-input").val($.URLDecode(requestedTumblr));
		go();
	}
    
 });

