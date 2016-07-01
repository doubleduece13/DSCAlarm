/**
 *  DSC Panic Key
 *
 *  Author: David de Gruyl <david.degruyl@gmail.com>
 *  Original Code By: Jordan <jordan@xeron.cc>, Rob Fisher <robfish@att.net>, 
 *                    Carlos Santiago <carloss66@gmail.com>, JTT <aesystems@gmail.com>
 *  Date: 2016-07-01
 */
 // for the UI

metadata {
    // Automatically generated. Make future change here.
    definition (name: "DSC Panic Key", author: "David de Gruyl <david.degruyl@gmail.com>", namespace: 'dsc') {
        capability "Switch"
        capability "Refresh"

        command "keypanic"
        command "key"
        command "nokey"
    }

    // simulator metadata
    simulator {
    }

    // UI tile definitions
    tiles(scale: 2) {
      standardTile("keypanic", "device.keypanic", width: 6, height: 4, title: "Panic Key", decoration:"flat"){
        state "restore", label: 'Panic Key', action: "keypanic", icon: "st.Transportation.transportation9", backgroundColor: "#FFFFFF"
        state "alarm", label: 'Panic Key Alarm', action: "keypanic", icon: "st.Transportation.transportation9", backgroundColor: "#000fd5"
      }

      main "keypanic"
      details(["keypanic"])
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

def keypanic() {
  if ("${device.currentValue("key")}" == 'key') {
    parent.sendUrl('panic?type=3')
  }
}

def nokey() {
  sendEvent (name: "key", value: "nokey")
}

def key() {
  sendEvent (name: "key", value: "key")
}