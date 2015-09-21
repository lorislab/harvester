var wsclient = {};
if (!wsclient.client) {    
    wsclient.client = {        
        clients: {},
        init: function (id, context, channel, update, message) {
            var tmp = this.clients[channel];
            if (!tmp) {
                var p = 'ws://';
                if (document.location.protocol === 'https:') {
                    p = 'wss://';
                }
                tmp = new WebSocket(p + document.location.host + context + '/' + channel);
                this.clients[channel] = tmp;
            }
            tmp.addEventListener('message', function (evt) {
                if (message.split(',').indexOf(evt.data) > -1) {
                    jsf.ajax.request(document.getElementById(id), event, {render: update});
                }
            });
        }
    };
}