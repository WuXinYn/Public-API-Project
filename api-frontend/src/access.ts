/**
 * 控制用户的访问权限
 * */
export default function access(initialState: InitialState | undefined) {
  const { loginUser } = initialState ?? {};
  return {
    canUser: loginUser,
    canAdmin: loginUser && loginUser.userRole === 'admin',
  };
}
