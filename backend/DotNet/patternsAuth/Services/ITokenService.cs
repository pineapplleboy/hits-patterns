namespace patternsAuth.Services
{
    public interface ITokenService
    {
        public string CreateAccessTokenById(Guid id, List<string> roles);
    }
}
