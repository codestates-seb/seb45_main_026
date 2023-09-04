import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'
import { useSelector } from 'react-redux';
import { RegularButton } from '../buttons/Buttons';
import { BodyTextTypo, SmallTextTypo } from '../typographys/Typographys';

const globalTokens = tokens.global;

export const InputContainer = styled.div`
    margin-top: ${props=>props.marginTop?props.marginTop:0}px;
    margin-bottom: ${props=>props.marginBottom?props.marginBottom:0}px;
    margin-left: ${props=>props.marginLeft?props.marginLeft:0}px;
    margin-right: ${props=>props.marginRight?props.marginRight:0}px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: start;
`
export const InputWithButtonContainer = styled.div`
    display: flex;
    flex-direction: row;
`
export const RegularInput = styled.input`
    width: ${ (props)=>props.width };
    background-color: rgba(255,255,255,0.25);
    padding: ${globalTokens.Spacing8.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${globalTokens.LightGray.value};
    font-size: ${globalTokens.BodyText.value}px;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
    &::placeholder {
        color: ${(props)=>props.isDark? globalTokens.LightGray.value : globalTokens.Gray.value};
    }
    &:focus {
        outline: ${globalTokens.RegularHeight.value}px solid ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Positive.value};
    }
`
export const InputButton = styled(RegularButton)`
    margin-left: ${globalTokens.Spacing4.value}px;
    padding: ${globalTokens.Spacing4.value}px;
`
export const InputErrorTypo = styled(SmallTextTypo)`
    width: ${props=>props.width?props.width:'95%'};
    color: ${props=>props.isDark ? globalTokens.LightRed.value : globalTokens.Negative.value};
    text-align: end;
`
export const InputPositiveTypo = styled(SmallTextTypo)` 
    width: ${props=>props.width?props.width:'95%'};
    color: ${(props)=>props.isDark ? globalTokens.LightNavy.value : globalTokens.Positive.value};
    text-align: end;
`
export const Input = ({
    marginTop, marginBottom, marginLeft, marginRight,
    label, name, type, placeholder, width,
    register, required, maxLength, minLength, pattern, validateFunc,
    isButton, buttonTitle, handleButtonClick, onChange
}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <InputContainer 
            marginTop={marginTop}
            marginBottom={marginBottom}
            marginLeft={marginLeft}
            marginRight={marginRight}>
        { label && 
                <BodyTextTypo isDark={isDark}>
                    {label}
                </BodyTextTypo> }
            <InputWithButtonContainer>
                <RegularInput
                    isDark={isDark}
                    width={ 
                        width? `${width}`
                        : isButton? '200px'
                        : '300px' }
                    type={type}
                    placeholder={placeholder}
                    {...register(name, { 
                        required: required,
                        maxLength: maxLength,
                        minLength: minLength,
                        pattern: pattern,
                        validate: validateFunc })}
                    onChange={onChange}/>
                    { isButton&&
                        <InputButton
                            type='button'
                            isDark={isDark}
                            onClick={handleButtonClick}>
                                {buttonTitle}
                        </InputButton>
                    }
            </InputWithButtonContainer>
        </InputContainer>
    );
}