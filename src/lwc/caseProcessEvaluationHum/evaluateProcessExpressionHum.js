const checkAndExpression = (expression) => {
    let expValues = expression.split('&&');
    let result = true;
    expValues.forEach(k => {
        if (k && (k.includes('&&') || k.includes('||'))) {
            if (k.includes('&&')) {
                result = checkAndExpression(k);
            } else if (k.includes('||')) {
                result = checkORExpression(k);
            }
            if (result != true) {
                return result;
            }
        } else {
            if (JSON.parse(k.trim()) != true) {
                result = false;
                return result;
            }
        }
    });
    return result;
}

const checkORExpression = (expression) => {
    let expValues = expression.split('||');
    let result = false;
    expValues.forEach(k => {
        if (k && (k.includes('&&') || k.includes('||'))) {
            if (k.includes('&&')) {
                result = checkAndExpression(k);
            } else if (k.includes('||')) {
                result = checkORExpression(k);
            }
            if (result != true) {
                return result;
            }
        } else {
            if (JSON.parse(k.trim()) === true) {
                result = true;
                return result;
            }
        }
    });
    return result;
}

const evaluateExpression = (expression) => {
    let finalResult = false;
    let mainExpression = '';
    let childExpression = '';
    let i = 0;
    let bIsBracesStared = false;
    if (expression) {
        if (!expression.includes('&&') && !expression.includes('||')) {
            if (JSON.parse(expression) === true) {
                finalResult = true;
            }
        } else if (!expression.includes('||') && !expression.includes('(') && !expression.includes(')')) {
            finalResult = checkAndExpression(expression);
        } else if (!expression.includes('&&') && !expression.includes('(') && !expression.includes(')')) {
            finalResult = checkORExpression(expression);
        }
        else {
            let expValues = expression.split(' ');
            if (expValues && Array.isArray(expValues) && expValues.length > 0) {
                while (i <= expValues.length - 1) {
                    if (expValues[i] && !expValues[i].includes('(') && !expValues[i].includes(')') && !bIsBracesStared) {
                        mainExpression += expValues[i].trim();
                    } else if (expValues[i] && expValues[i].includes('(') && !expValues[i].includes(')')) {
                        bIsBracesStared = true;
                        childExpression += expValues[i].replaceAll('(', '');
                    } else if (expValues[i] && (expValues[i].trim() === '&&' || expValues[i].trim() === '||') && bIsBracesStared) {
                        childExpression += expValues[i].trim();
                    } else if (expValues[i] && expValues[i].trim() != '&&' && expValues[i].trim() != '||'
                        && !expValues[i].includes('(') && !expValues[i].includes(')')) {
                        childExpression += expValues[i].trim();
                    }
                    else if (expValues[i] && expValues[i].includes(')') && !expValues[i].includes('(')) {
                        childExpression += expValues[i].replaceAll(')', '');
                        bIsBracesStared = false;
                        mainExpression += childExpression.includes('&&') ? checkAndExpression(childExpression) : childExpression.includes('||') ? checkORExpression(childExpression) : '';
                        childExpression = '';
                    }
                    i++;
                }
            }
            finalResult = mainExpression.includes('&&') ? checkAndExpression(mainExpression) :
                mainExpression.includes('||') ? checkORExpression(mainExpression) : false;
        }
    } else {
        finalResult = true;
    }
    return finalResult;
}

export { evaluateExpression }