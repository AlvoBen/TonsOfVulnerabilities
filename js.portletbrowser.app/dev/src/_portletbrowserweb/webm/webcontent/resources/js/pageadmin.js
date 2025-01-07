function addButtonAction(action, form){
	var input = document.createElement('input');
	input.setAttribute('type','hidden');
	input.setAttribute('name','action');
	input.setAttribute('value',action);
	var node = form.appendChild(input);
}

function changeButtonValue(button,action,value){
    action = action+":"+value;
	button.value = action;
}


