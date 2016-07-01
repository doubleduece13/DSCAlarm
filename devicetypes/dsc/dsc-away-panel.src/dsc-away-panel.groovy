/**
 *  DSC Fire Key
 *
 *  Author: David de Gruyl <david.degruyl@gmail.com>
 *  Original Code By: Jordan <jordan@xeron.cc>, Rob Fisher <robfish@att.net>, 
 *                    Carlos Santiago <carloss66@gmail.com>, JTT <aesystems@gmail.com>
 *  Date: 2016-07-01
 */
 // for the UI

metadata {
    // Automatically generated. Make future change here.
    definition (name: "DSC Fire Key", author: "David de Gruyl <david.degruyl@gmail.com>", namespace: 'dsc') {
        capability "Switch"
        capability "Refresh"

        command "keyfire"
        command "key"
        command "nokey"
    }

    // simulator metadata
    simulator {
    }

    // UI tile definitions
    tiles(scale: 2) {
      standardTile("keyfire", "device.keyfire", width: 6, height: 4, title: "Fire Key", decoration:"flat"){
        state "restore", label: 'Fire Key', action: "keyfire", icon: "st.Home.home29", backgroundColor: "#FFFFFF"
        state "alarm", label: 'Fire Key Alarm', action: "keyfire", icon: "st.Home.home29", backgroundColor: "#FF2400"
      }

      main "keyfire"
      details(["keyfire"])
    }
}
def partition(String state, String partition, Map parameters) {
 if (state.startsWith('key')) {
    def name = state.minus('alarm').minus('restore')
    def value = state.replaceAll(/.*(alarm|restore)/, '$1')
    sendEvent (name: "${name}", value: "${value}")
  } else {
    // Send final event
    sendEvent (name: "status", value: "${state}")
  }
}

def keyfire() {
  if ("${device.currentValue("key")}" == 'key') {
    parent.sendUrl('panic?type=1')
  }
}

def nokey() {
  sendEvent (name: "key", value: "nokey")
}

def key() {
  sendEvent (name: "key", value: "key")
}