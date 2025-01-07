import { pubsub } from 'c/pubsubComponent';

export function optionToggler(e) {
    this.errorTrace.stack = 'Method: optionToggler, file: demographicDriverHelper.js';
    for (const option of this.demographicUpdateOption) {
        if(option.visible) {
            if(option.order === +e.target.dataset.id) {
                option.selected = e.target.checked;
            }
            else {
                if(!this.sequence) {
                    option.selected = false;
                    this.template.querySelector(`[data-id="${option.order}"]`).checked = false;
                }
            }
        }
    }
}

export function flowListenerGenerator() {
    this.errorTrace.stack = 'Method: flowListenerGenerator, file: demographicDriverHelper.js';
    for (const option of this.demographicUpdateOption) {
        if(option.visible) {
            this.flowListener.push({
                order: option.order,
                status: (typeof option.status === "undefined") ? 'Not Selected' : option.status,
                title: option.title,
                avf: option.isAVF,
                eventName: option.eventName,
                value: option.value
            });
        }
    }
}

export function flowListenerProcessor(e) {
    this.errorTrace.stack = 'Method: flowListenerProcessor, file: demographicDriverHelper.js';
    const elementOrder = +e.target.dataset.id;
    deriveFlowStatus.apply(this, [elementOrder]);
}

export function deriveFlowStatus(elementOrder) {
    this.errorTrace.stack = 'Method: deriveFlowStatus, file: demographicDriverHelper.js';
    let inProgressFlag = false;

    for (const option of this.demographicUpdateOption) {
        if(option.status === 'Complete') {
            for (const item of this.flowListener) {
                if(item.order === option.order) {
                    item.status = 'Complete';
                    break;
                }
            }
        }
    }

    for (const option of this.demographicUpdateOption) {
        if(!option.selected && option.status !== 'Complete') {
            for (const item of this.flowListener) {
                if(item.order === option.order) {
                    item.status = 'Not Selected';
                    break;
                }
            }
        }
    }

    for (const option of this.demographicUpdateOption) {
        if(option.selected && option.order === elementOrder && option.status !== 'Complete') {
            for (const item of this.flowListener) {
                if(item.order === option.order) {
                    item.status = 'Not Started'; //from Not Selected
                    break;
                }
            }
        }
    }

    for (const option of this.demographicUpdateOption) {
        if(option.selected && option.order !== elementOrder && option.status !== 'Complete') {
            for (const item of this.flowListener) {
                if(item.order === option.order) {
                    if(!item.avf && item.status === 'In Progress') {
                        inProgressFlag = true;
                    }
                    else if(item.avf && item.status === 'In Progress') {
                        item.status = 'Not Started';
                    }
                    else if(!item.avf && item.status !== 'In Progress') {
                        inProgressFlag = (inProgressFlag === true) ? true : false;
                    }
                    break;
                }
            }
        } 
    }

    if(!inProgressFlag) {
        for(const item of this.flowListener) {
            if(item.status === 'Not Started') {
                item.status = 'In Progress';
                break;
            }
        }
    }
}

export function emitToggleIndex() {
    this.errorTrace.stack = 'Method: emitToggleIndex, file: demographicDriverHelper.js';
    let index = 0;
    let isAvf = false;
    let inProgressCount = 0;
    let keyName = '';

    for (const item of this.flowListener) {
        if(item.status === 'In Progress') {
            index = item.order;
            isAvf = item.avf;
            keyName = item.value;
            inProgressCount++;
            break;
        }
    }

    if(isAvf) {
        pubsub.publish('toggleFields', {
            detail: { 
                index: index
            }
        });
        pubsub.publish('toggleNextBtn', {
            detail: { 
                data: false
            }
        });
        pubsub.publish('toggleHeirarchyMessage', {
            detail: {
                keyName: keyName,
                display: false
            }
        });
    }
    else {
        if(inProgressCount > 0) {
            pubsub.publish('toggleFields', {
                detail: { 
                    index: index
                }
            });
        }
        else {
            pubsub.publish('toggleFields', {
                detail: { 
                    index: 0
                }
            });
            pubsub.publish('toggleNextBtn', {
                detail: { 
                    data: true
                }
            });
        }
    }
    
}