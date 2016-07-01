/**
 *  DSC Aux Key
 *
 *  Author: David de Gruyl <david.degruyl@gmail.com>
 *  Original Code By: Jordan <jordan@xeron.cc>, Rob Fisher <robfish@att.net>, 
 *                    Carlos Santiago <carloss66@gmail.com>, JTT <aesystems@gmail.com>
 *  Date: 2016-07-01
 */
 // for the UI

metadata {
    // Automatically generated. Make future change here.
    definition (name: "DSC Aux Key", author: "David de Gruyl <david.degruyl@gmail.com>", namespace: 'dsc') {
        capability "Switch"
        capability "Refresh"

        command "keyaux"
        command "key"
        command "nokey"
    }

    // simulator metadata
    simulator {
    }

    // UI tile definitions
    tiles(scale: 2) {
      standardTile("keyaux", "device.keyaux", width: 6, height: 4, title: "Aux Key", decoration:"flat"){
        state "restore", label: 'Aux Key', action: "keyaux", icon: "st.Transportation.transportation7", backgroundColor: "#FFFFFF"
        state "alarm", label: 'Aux Key Alarm', action: "keyaux", icon: "st.Transportation.transportation7", backgroundColor: "#DD0000"
      }

      main "keyaux"
      details(["keyaux"])
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

def keyaux() {
  if ("${device.currentValue("key")}" == 'key') {
    parent.sendUrl('panic?type=2')
  }
}

def nokey() {
  sendEvent (name: "key", value: "nokey")
}

def key() {
  sendEvent (name: "key", value: "key")
}