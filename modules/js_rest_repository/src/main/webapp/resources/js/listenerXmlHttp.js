function getEntityXmlHttpRequest(httpMethod) {
    let url = getURL(httpMethod);
    let req = new XMLHttpRequest();
    let userJson = null;
    if (httpMethod === 'POST' || httpMethod === 'PUT') {
        userJson = JSON.stringify(getEntity(httpMethod.toLowerCase()));
    }
    req.open(httpMethod, url, true);
    req.send(userJson);
    return req;
}

function responseToHtml(elementId, httpMethod) {
    let element = document.getElementById(elementId);
    let resp = getEntityXmlHttpRequest(httpMethod);
    resp.onload = function (e) {
        if (resp.readyState === 4) {
            let html = '<p>' + resp.responseText + '</p>';
            element.innerHTML += html;
        }
    }
}

const sendGetUserRequest = event => {
    event.preventDefault();
    let getUserInfoDiv = document.getElementById("get_entity_info");
    let resp = getEntityXmlHttpRequest('GET');
    resp.onload = function (e) {
        if (resp.readyState === 4) {
            let httpStatus = resp.status;
            let html;
            if (httpStatus === 200) {
                let entity = JSON.parse(resp.responseText);
                html = '<table border="1"><tr>';
                Object.keys(entity).forEach(e => html += '<td>' + entity[e] + '</td>')
                html += '</tr></table>';
            } else {
                html = '<p>' + resp.responseText + '</p>';
            }
            getUserInfoDiv.innerHTML += html;
        }
    };
}

const sendDeleteUserRequest = event => {
    event.preventDefault();
    responseToHtml("delete_entity_info", 'DELETE');
}

const sendSaveUserRequest = event => {
    event.preventDefault();
    responseToHtml("post_entity_info", 'POST');
}

const sendUpdateUserRequest = event => {
    event.preventDefault();
    responseToHtml("put_entity_info", 'PUT');
}

document.querySelector('.get_button').addEventListener('click', sendGetUserRequest, false);
document.querySelector('.post_button').addEventListener('click', sendSaveUserRequest, false);
document.querySelector('.delete_button').addEventListener('click', sendDeleteUserRequest, false);
document.querySelector('.put_button').addEventListener('click', sendUpdateUserRequest, false);