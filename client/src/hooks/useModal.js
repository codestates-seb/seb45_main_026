export const useModal = ({
    isPositiveClick,
    handleNegativeClick,
    handlePositiveClick,
}) => {
    if(isPositiveClick) {
        return handlePositiveClick();
    } else {
        return handleNegativeClick();
    }
}