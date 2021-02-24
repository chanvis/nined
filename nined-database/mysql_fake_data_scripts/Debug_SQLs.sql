select ur.USER_ID, r.NAME, p.NAME from role_privilege rp, role r, user_role ur, privilege p 
where r.role_id =  ur.role_id and r.role_id = rp.role_id and p.PRIVILEGE_ID = rp.PRIVILEGE_ID and  ur.user_id = 9; ;
