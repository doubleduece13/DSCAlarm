/*
 *  DSC Zone Flood Device
 *
 *  Author: Jordan <jordan@xeron.cc>
 *  Original Author: Matt Martz <matt.martz@gmail.com>
 *  Modified to be a motion device: Kent Holloway <drizit@gmail.com>
 *  Date: 2016-02-27
 *
 *  Modified: David de Gruyl <david.degruyl@gmail.com>
 *  Add Tamper Alert
 *  Date: 2016-06-30
 */

// for the UI
metadata {
  definition (name: "DSC Zone Flood", author: "jordan@xeron.cc", namespace: 'dsc') {
    // Change or define capabilities here as needed
    capability "Water Sensor"
    capability "Sensor"
    capability "Momentary"
    capability "Tamper Alert"

    // Add commands as needed
    command "zone"
    command "bypass"
  }

  simulator {
    // Nothing here, you could put some testing stuff here if you like
  }

  tiles(scale: 2) {
    multiAttributeTile(name:"zone", type: "generic", width: 6, height: 4) {
      tileAttribute ("device.water", key: "PRIMARY_CONTROL") {
        attributeState "wet", label:'wet', icon:"st.alarm.water.wet", backgroundColor:"#53a7c0"
        attributeState "dry", label:'dry', icon:"st.alarm.water.dry", backgroundColor:"#ffffff"
        attributeState "alarm", label:'ALARM', icon:"st.alarm.water.wet", backgroundColor:"#ff0000"
      }
      tileAttribute ("device.trouble", key: "SECONDARY_CONTROL") {
        attributeState "restore", label: 'No Trouble', icon: "st.security.alarm.clear"
        attributeState "tamper", label: 'Tamper', icon: "st.security.alarm.alarm"
        attributeState "fault", label: 'Fault', icon: "st.security.alarm.alarm"
      }
    }
    standardTile("bypass", "device.bypass", width: 2, height: 2, title: "Bypass Status", decoration:"flat"){
      state "off", label: 'Enabled', icon: "st.security.alarm.on"
      state "on", label: 'Bypassed', icon: "st.security.alarm.off"
    }
    standardTile("bypassbutton", "capability.momentary", width: 2, height: 2, title: "Bypass Button", decoration: "flat"){
      state "bypass", label: 'Bypass', action: "bypass", icon: "st.locks.lock.unlocked"
    }
    valueTile("tamperAlert", "device.tamper", width: 2, height: 2) {
	  state "clear", label:"Tamper Clear", backgroundColor: "#CCCCCC"
	  state "detected", label:"Tamper Detected", backgroundColor: "#FF0000"
	}

    // This tile will be the tile that is displayed on the Hub page.
    main "zone"

    // These tiles will be displayed when clicked on the device, in the order listed here.
    details(["zone", "bypass", "bypassbutton","tamperAlert"])
  }
}

// handle commands
def bypass() {
  def zone = device.deviceNetworkId.minus('dsczone')
  parent.sendUrl("bypass?zone=${zone}")
}

def push() {
  bypass()
}

def zone(String state) {
  // state will be a valid state for a zone (open, closed)
  // zone will be a number for the zone
  log.debug "Zone: ${state}"

  def troubleList = ['fault','tamper','restore']
  def bypassList = ['on','off']
  
  if (state == "tamper") {
    sendEvent(getTamperEventMap("detected"))
  } else {
    sendEvent(getTamperEventMap("clear"))
  }
  
  if (troubleList.contains(state)) {
    // Send final event
    sendEvent (name: "trouble", value: "${state}")
  } else if (bypassList.contains(state)) {
    sendEvent (name: "bypass", value: "${state}")
  } else {
    // Since this is a water sensor device we need to convert open to active and closed to inactive
    // before sending the event
    def eventMap = [
     'open':"wet",
     'closed':"dry",
     'alarm':"alarm"
    ]
    def newState = eventMap."${state}"
    // Send final event
    sendEvent (name: "water", value: "${newState}")
  }
}

private clearTamperDetected() {	
	if (device.currentValue("tamper") != "clear") {
		logDebug "Resetting Tamper"
		sendEvent(getTamperEventMap("clear"))			
	}
}

def getTamperEventMap(val) {
	[
		name: "tamper", 
		value: val, 
		isStateChange: true, 
		displayed: (val == "detected"),
		descriptionText: "Tamper is $val"
	]
}
