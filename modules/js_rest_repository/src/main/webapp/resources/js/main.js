function getURL(httpMethod) {
    let classPrefix = httpMethod.toLowerCase()
    let id = document.getElementById(classPrefix + "_id").value;
    let urlRoot = document.URL.substring(0, document.URL.indexOf("resources"));
    let urlSuffix = document.URL.substring(document.URL.lastIndexOf("/") + 1, document.URL.lastIndexOf(".")) + "?id=";
    return urlRoot + urlSuffix + id;
}

function getEntity(httpMethod) {
    let form = document.getElementById(httpMethod.toLowerCase() + "_form");
    let entity = {};
    createEntity(form, entity);
    return entity;

    function createEntity(node, entity) {
        node.childNodes.forEach((subNode) => {
            if (subNode.nodeName === 'INPUT') {
                entity[subNode.id.substring(subNode.id.indexOf("_") + 1)] = subNode.value;
            } else {
                createEntity(subNode, entity);
            }
        });
    }
}