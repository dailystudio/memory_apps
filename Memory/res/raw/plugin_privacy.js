function createPrivacyCollectionElement(plgPrivacy) {
	if (plgPrivacy == null || plgPrivacy.collection == null) {
		return null;
	}
	
	var elmPrivacyCollection = document.createElement("li");

	var elmPlgName = document.createElement("font");
	elmPlgName.color="1010EF";
	var elmTextName = document.createTextNode(plgPrivacy.label);
	elmPlgName.appendChild(elmTextName);
	
	var elmCollection = document.createTextNode(":" + plgPrivacy.collection);
		
	elmPrivacyCollection.appendChild(elmPlgName);
	elmPrivacyCollection.appendChild(elmCollection);

	return elmPrivacyCollection;
}

function loadPluginsPrivacy() {
	var components = eval(window.plgmgr.getPluginPackages());

	var plgCollections = document.getElementById("plugin_collections");

	plgCollections.innerHTML = ""; 	
	
	for (var i = 0; i < components.length; i++) {
		console.log("components[" + i + "]");
		var component = components[i];
		var privacyStr = window.plgmgr.getPrivacy(component);
		var pluginPrivacy = eval('(' + privacyStr + ')');

		var elmPrivacy = createPrivacyCollectionElement(pluginPrivacy);
		if (elmPrivacy == null) {
			continue;
		}
		
		plgCollections.appendChild(elmPrivacy);
	}

}	

function loadPluginsPrivacyLite() {
	var components = eval(window.plgmgr.getPluginPackages());

	var plgCollections = document.getElementById("plugin_collections");

	plgCollections.innerHTML = ""; 	
}	
