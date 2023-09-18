import { styled } from "styled-components";
import tokens from '../../styles/tokens.json'
import { InputButton, InputContainer, InputWithButtonContainer } from "./Inputs";
import { useSelector } from "react-redux";
import { BodyTextTypo } from "../typographys/Typographys";

const globalTokens = tokens.global;

export const RegularTextArea = styled.textarea`
    width: ${ (props)=>props.width };
    background-color: rgba(255,255,255,0.25);
    padding: ${globalTokens.Spacing8.value}px;
    border-radius: ${globalTokens.RegularRadius.value}px;
    border: ${globalTokens.ThinHeight.value}px solid ${props=>props.isDark? globalTokens.Gray.value : globalTokens.LightGray.value};
    font-size: ${globalTokens.BodyText.value}px;
    color: ${(props)=>props.isDark ? globalTokens.White.value : globalTokens.Black.value};
    &::placeholder {
        color: ${props=>props.isDark?globalTokens.LightGray.value:globalTokens.Gray.value};
    }
`

export const Textarea = ({
    marginTop, marginBottom, marginLeft, marginRight,
    label, labelDirection, name, type, placeholder, width,
    register, required, maxLength, minLength, pattern, validateFunc,
    isButton, buttonTitle, handleButtonClick, onChange, disabled
}) => {
    const isDark = useSelector(state=>state.uiSetting.isDark);

    return (
        <InputContainer
            labelDirection={labelDirection}
            marginTop={marginTop}
            marginBottom={marginBottom}
            marginLeft={marginLeft}
            marginRight={marginRight}>
            { label &&
                <BodyTextTypo isDark={isDark}>
                    {label}
                </BodyTextTypo>
            }
            <InputWithButtonContainer>
            <RegularTextArea
                isDark={isDark}
                width={
                    width? `${width}`
                        : isButton? '200px'
                        : '300px'
                }
                placeholder={placeholder}
                {...register(name, { 
                    required: required,
                    maxLength: maxLength,
                    minLength: minLength,
                    pattern: pattern,
                    validate: validateFunc })}
                onChange={onChange}
                disabled={disabled?disabled:false}/>
            { isButton &&
                <InputButton
                    type='button'
                    isDark={isDark}
                    onClick={handleButtonClick}>
                    {buttonTitle}
                </InputButton>
            }
            </InputWithButtonContainer>
        </InputContainer>
    )
}