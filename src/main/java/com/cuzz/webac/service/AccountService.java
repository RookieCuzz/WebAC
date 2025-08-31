package com.cuzz.webac.service;


import com.cuzz.webac.caches.Caches;
import com.cuzz.webac.mapper.AccountDOMapper;
import com.cuzz.webac.model.doo.AccountDO;
import com.cuzz.webac.model.dto.LoginAccountDTO;
import com.cuzz.webac.model.dto.RegisterAccountDTO;
import com.cuzz.webac.utils.PasswordUtils;
import com.cuzz.webac.utils.constants.RedisConstants;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class AccountService {

    @Resource
    private RedisService redisService;


    @Resource
    private AccountDOMapper accountDOMapper;

    @Resource
    private Caches caches;

    @Resource
    JwtService jwtService;

    /**
     *
     * @param acdto  账户信息
     * @return
     * @throws Exception
     */

    public AccountDO registerAccount(RegisterAccountDTO acdto) throws Exception {
        AccountDO accountDO = new AccountDO();
        accountDO.setNamex(acdto.getUsername());
        accountDO.setEmail(acdto.getUsername());
        accountDO.setRoleId(0);
        accountDO.setStatusx(0);

//        String hashPassword = getHashPassword(acdto.getPassword());
        accountDO.setPassword(accountDO.getPassword());
        accountDOMapper.insertSelective(accountDO);
        return accountDO;
    }

    /**
     *
     * @param email 邮箱
     * @return 账号实体
     */

    public AccountDO getAccountByEmail(String email){


        AccountDO accountDO = accountDOMapper.selectByEmail(email);
        return accountDO;

    }


    /**
     *
     * @param phone 用户名就是用户注册时的手机号
     * @return
     */
    public AccountDO getAccountByUserName(String phone){


        AccountDO accountDO = accountDOMapper.selectByUserName(phone);
        return accountDO;

    }


    /**
     *
     * @param accountDO  账户信息
     * @return
     */
    public boolean isAccountExist(AccountDO accountDO){

        return accountDO!=null;
    }

    /**
     *
     * @param accountDO 数据库账户
     * @param inputPassword 玩家输入的AES密码
     * @return
     */
    public boolean isPasswordCorrect(AccountDO accountDO,String inputPassword){


        try {
            String decryptedPassword  = PasswordUtils.decrypt(inputPassword);
            return PasswordUtils.checkPassword(decryptedPassword ,accountDO.getPassword());

        }catch (Exception e){
            return false;
        }

    }

//    /**
//     * 检查邮箱是否已存在
//     * @param email 邮箱 (一个账户对应一个唯一邮箱)
//     * @return true 如果邮箱已存在，false 如果邮箱不存在
//     */
//    public boolean isAccountExist(String email) {
//        // 构建查询条件
//        AccountDOExample accountDOExample = new AccountDOExample();
//        AccountDOExample.Criteria criteria =accountDOExample.createCriteria();
//        criteria.andEmailEqualTo( email);
//
//        // 查询数据库，检查是否存在记录
//        List<AccountDO> accounts = accountDOMapper.selectByExample(accountDOExample);
//
//        // 判断是否查询到数据，如果有数据则表示邮箱存在
//        return accounts != null && !accounts.isEmpty();
//    }

    /**
     *
     * @param string 前端传来的通过AES加密的密码
     * @return  通过JBCPCRYPT单向加密的密码
     * @throws Exception
     */
    public String getHashPassword(String string) throws Exception {

        String decrypt = PasswordUtils.decrypt(string);
        String hashPassword = PasswordUtils.hashPassword(decrypt);

        return hashPassword;
    }

    public boolean isValidRegisterInfo(RegisterAccountDTO acdto){
        //用户名不为空
        if (acdto.getUsername()==null||acdto.getUsername().equalsIgnoreCase(""))
            return false;
        if (!acdto.getPassword().equals(acdto.getRepeatPassword()))
            return  false;
        return true;
    }

    public boolean isEmailMatchCode(LoginAccountDTO accountDTO){
        System.out.println("redis- key: "+RedisConstants.getOrderKey(accountDTO.getUsername()));
        String cacheCode = (String) caches.getCache(RedisConstants.getOrderKey(accountDTO.getUsername()));

        if (cacheCode==null){
            return false;
        }
        if (cacheCode.equalsIgnoreCase(accountDTO.getVerificationCode())){
            return true;
        }
        return false;
    }
    public boolean isPhoneMatchCode(String phone,String code){
        String cacheCode = caches.getKey("SmsCache:" + phone);
        //null
        if (cacheCode==null){
            return false;
        }
        //空
        if (cacheCode.isBlank()){
            return false;
        }


        return cacheCode.equalsIgnoreCase(code);
    }
    public String generateApiKey(String str){

        String apiKey = jwtService.generateToken(str);


        return apiKey;
    }

}
