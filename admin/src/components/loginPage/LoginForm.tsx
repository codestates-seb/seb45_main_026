import { FormContainer, InputContainer, InputErrorTypo, LoginContainer, LoginButton, LoginTitleTypo } from './LoginForm.style';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../redux/Store';
import { RegularInput } from '../../atoms/inputs/Input.style';
import { FormProvider, SubmitHandler, useForm } from 'react-hook-form';
import { getUserInfoService, loginService } from '../../services/loginService';
import { setAccessToken, setIsLogin, setMyId, setLoginInfo } from '../../redux/createSlice/loginInfoSlice';
import { useNavigate } from 'react-router-dom';

interface formValue {
    email: string,
    password : string,
}

const LoginForm = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const isDark = useSelector((state:RootState)=>state.uiSetting.isDark);
    
    const method = useForm<formValue>({
        mode : 'all'
    });

    const {
        register,
        handleSubmit, 
        formState: { errors }
    } = method;

    const onSubmit : SubmitHandler<formValue> = async (data) => {
        const response = await loginService( data.email, data.password );
        if(response.status==='success') {
            //유저 정보 조회 후 관리자가 아니면 로그인 실패
            const authorization = response.authorization;
            const refresh = response.refresh;

            getUserInfoService(authorization).then((res)=>{
                if(res.status==='success' && res.data.data.authority==='ROLE_ADMIN') {
                    dispatch(setLoginInfo({
                        email: res.data.data.email,
                        nickname: res.data.data.nickname,
                        grade: res.data.data.grade,
                        imgUrl: res.data.data.imageUrl,
                        reward: res.data.data.reward,
                        authority: res.data.data.authority,
                    }));
                    dispatch(setAccessToken({
                        authorization: authorization,
                        refresh: refresh,
                    }));
                    dispatch(setMyId(res.data.data.memberId));
                    dispatch(setIsLogin(true));
                    navigate('/');
                } else {
                    console.log(response.data)
                }
            })
        } else {
            console.log(response.data);
        }
    }

    return (
        <LoginContainer isDark={isDark}>
            <LoginTitleTypo isDark={isDark}>로그인</LoginTitleTypo>
            <FormProvider {...method}>
                <FormContainer onSubmit={handleSubmit(onSubmit)}>
                    <InputContainer>
                        <RegularInput 
                            type='text'
                            width='250px'
                            isDark={isDark}
                            placeholder='이메일을 입력해 주세요.'
                            {...register('email',{
                                required: true,
                                maxLength: 30,
                                minLength: 6,
                                pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/i
                            })}/>
                        { errors.email && errors.email.type==='required' &&
                            <InputErrorTypo isDark={isDark}>이메일을 입력해 주세요!</InputErrorTypo> }
                        { errors.email && errors.email.type==='pattern' &&
                            <InputErrorTypo isDark={isDark}>올바르지 않은 이메일 형식입니다.</InputErrorTypo> }
                    </InputContainer>
                    <InputContainer>
                        <RegularInput 
                            type='password'
                            width='250px'
                            isDark={isDark}
                            maxLength={20}
                            placeholder='비밀번호를 입력해 주세요.'
                            {...register('password',{
                                required: true,
                                maxLength: 20,
                                minLength: 9,
                                pattern: /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$/
                            })}/>
                        { errors.password && errors.password.type==='required' &&
                            <InputErrorTypo isDark={isDark}>비밀번호를 입력해 주세요.</InputErrorTypo> }
                        { errors.password && errors.password.type==='minLength' && 
                            <InputErrorTypo isDark={isDark}>비밀번호는 9자 이상입니다.</InputErrorTypo>}
                    </InputContainer>
                    <LoginButton isDark={isDark} type='submit'>로그인</LoginButton>
                </FormContainer>
            </FormProvider>
        </LoginContainer>
    );
};

export default LoginForm;