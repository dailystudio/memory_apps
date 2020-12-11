function initAndCheckApps() {
	loadPromoteApps();
	checkAppsExistence();
}

function loadPromoteApps() {
	var appsJson = window.promoteAppsInterface.getPromoteAppsInJson();
	console.log("appsJson = " + appsJson);
	var promoteapps = eval('(' + appsJson + ')');
	var applist = promoteapps.apps;
	
	var appsContainer = document.getElementById("apps_container");
	
	appsContainer.innerHTML = "";
	
	for (var i = 0; i < applist.length; i++) {
		var promoteapp = applist[i];
		console.log("promoteapp[" + i + "] = " + promoteapp);

		var elmPromoteApp = createPromoteAppElement(i, promoteapp);
		if (elmPromoteApp == null) {
			continue;
		}
		
		appsContainer.appendChild(elmPromoteApp);
	}

}	

function createPromoteAppElement(pid, promoteapp) {
	if (promoteapp == null) {
		return null;
	}
	
	var nid = "promoteapp" + pid;
	var imgid = nid + ".img";
	
	var disableIcon = "file:///android_asset/images/" + promoteapp.disabledIconRes + ".png";
	var clickEvent = "link_to_market('" + promoteapp.packageName + "')";

	var divNode = document.createElement("div");
	divNode.innerHTML = 
		"<li id=\"" + nid + "\" onclick=\"" + clickEvent + "\">" +
		"<center><img src=\"" + disableIcon + "\" id=\"" + imgid + "\" >" + 
		"<p>" + promoteapp.appName + "</p>" +
		"</img></center></li>";
		
	return divNode;
}

function checkAppsExistence() {
	var appsJson = window.promoteAppsInterface.getPromoteAppsInJson();
	console.log("appsJson = " + appsJson);
	var promoteapps = eval('(' + appsJson + ')');
	var applist = promoteapps.apps;
	
	var installed = false;
	for (var i = 0; i < applist.length; i++) {
		installed =	
			plgmgr.isPluginInastalled(applist[i].packageName);
				
		setPromoteAppEnabled(i, applist[i], installed);
	}
}

function setPromoteAppEnabled(pid, promoteapp, enabled) {
	if (promoteapp == null) {
		return null;
	}
	
	var nid = "promoteapp" + pid;
	var imgid = nid + ".img";
	
	var disableIcon = "file:///android_asset/images/" + promoteapp.disabledIconRes + ".png";
	var enableIcon = "file:///android_asset/images/" + promoteapp.enabledIconRes + ".png";
	
	if (enabled) {
		icsrc = enableIcon;
	} else {
		icsrc = disableIcon;
	}
	
	console.log("icsrc = " + icsrc);
	
	document.getElementById(imgid).src = icsrc;
}

function link_to_market(appPackage){
	var marketurl = "market://details?id=" + appPackage;

	window.open(marketurl);
}

