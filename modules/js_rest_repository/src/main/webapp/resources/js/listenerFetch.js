async function getEntityRequest(httpMethod) {
    let url = getURL(httpMethod);
    let body = null;
    if (httpMethod === 'POST' || httpMethod === 'PUT') {
        body = JSON.stringify(getEntity(httpMethod.toLowerCase()));
    }
    return await fetch(url, {
        method: httpMethod,
        mode: 'cors',
        cache: 'no-cache',
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json'
        },
        redirect: 'follow',
        referrerPolicy: 'no-referrer',
        body: body
    });
}

const sendGetUserRequest = async event => {
    event.preventDefault();
    let getUserInfoDiv = document.getElementById("get_entity_info");
    let resp = await getEntityRequest('GET');
    let httpStatus = resp.status;
    let html;
    if (httpStatus === 200) {
        let entity = JSON.parse(await resp.text());
        html = '<table border="1"><tr>';
        Object.keys(entity).forEach(e => html += '<td>' + entity[e] + '</td>')
        html += '</tr></table>';
    } else {
        html = '<p>' + await resp.text() + '</p>';
    }
    getUserInfoDiv.innerHTML += html;
}

async function responseToHtml(elementId, httpMethod) {
    let element = document.getElementById(elementId);
    let resp = await getEntityRequest(httpMethod);
    let html = '<p>' + await resp.text() + '</p>';
    element.innerHTML += html;
}

const sendSaveUserRequest = async event => {
    event.preventDefault();
    await responseToHtml("post_entity_info", 'POST');
}

const sendUpdateUserRequest = async event => {
    event.preventDefault();
    await responseToHtml("put_entity_info", 'PUT');
}

const sendDeleteUserRequest = async event => {
    event.preventDefault();
    await responseToHtml("delete_entity_info", 'DELETE');
}

document.querySelector('.get_button').addEventListener('click', sendGetUserRequest, false);
document.querySelector('.post_button').addEventListener('click', sendSaveUserRequest, false);
document.querySelector('.delete_button').addEventListener('click', sendDeleteUserRequest, false);
document.querySelector('.put_button').addEventListener('click', sendUpdateUserRequest, false);