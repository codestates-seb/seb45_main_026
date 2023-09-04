import React from 'react';
import { styled } from 'styled-components';
import tokens from '../../styles/tokens.json'
import { BodyTextTypo } from '../typographys/Typographys'

const globalTokens = tokens.global;

export const Icon = styled.svg`
	fill: none;
	stroke: white;
	stroke-width: 2px;
`
export const CheckboxContainer = styled.div`
    display: inline-block;
	vertical-align: middle;
    cursor: pointer;
`
export const CheckboxDesign = styled.div`
    display: inline-block;
	width: 2rem;
	height: 2rem;
	border: ${ (props) => props.isChecked ? 'none' : `solid 1px ${globalTokens.LightGray.value}`};
	background: ${ (props) => 
        props.isChecked && props.isDark ? globalTokens.LightNavy.value
        : props.isChecked ? globalTokens.MainRed.value
        : 'rgba(255,255,255,0.25)'};
	border-radius: 0.4rem;
	transition: 150ms;
    ${Icon} {
		visibility: ${props=>props.isChecked? 'visible': 'hidden'};
	}
`
export const CheckboxInput = styled.input`
    border: 0;
	clip: rect(0 0 0 0);
	/* clippath: inset(50%); */
	height: 1px;
	margin: -1px;
	overflow: hidden;
	padding: 0;
	position: absolute;
	white-space: nowrap;
	width: 1px;
`
export const CheckboxLabel = styled(BodyTextTypo)`
    margin-left: ${globalTokens.Spacing8.value}px;
`

export const Checkbox = ({isDark, isChecked, setIsChecked}) => (
    <CheckboxContainer onClick={setIsChecked}>
		<CheckboxInput type='checkbox' isChecked={isChecked} isDark={isDark}/>
		<CheckboxDesign isChecked={isChecked} isDark={isDark}>
			<Icon viewBox="0 0 24 24">
				<polyline points="19 7 10 17 5 12"/>
			</Icon>
		</CheckboxDesign>
	</CheckboxContainer>
)