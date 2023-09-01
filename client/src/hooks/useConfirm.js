import React from 'react';

const useConfirm = (message=null, onConfirm=()=>{}, onCancel=()=>{}) => {
    const confirmAction = () => {
        if(window.confirm(message)) {
            onConfirm();
        } else {
            onCancel();
        }
    }

    return confirmAction;
};

export default useConfirm;