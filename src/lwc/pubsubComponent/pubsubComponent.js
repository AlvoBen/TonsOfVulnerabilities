const pubsub = (function() {

    let topics = {};
    let hOP = topics.hasOwnProperty;

    return {
        subscribe: function(topic, listener) {
            if(!hOP.call(topics, topic)) {
                topics[topic] = [];
            }

            let index = topics[topic].push(listener) - 1;

            return {
                remove: function() {
                    delete topics[topic][index];
                }
            }
        },
        publish: function(topic, info) {
            if(!hOP.call(topics, topic)) {
                return;
            }

            topics[topic].forEach(function(item) {
                item(info !== undefined ? info : {});
            });
        },
		unsubcribe: function(){
            topics = {};
            return null;
        }
    }

})();

export {
    pubsub
}