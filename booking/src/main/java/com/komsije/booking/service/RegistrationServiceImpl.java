package com.komsije.booking.service;

import com.komsije.booking.dto.RegistrationDto;
import com.komsije.booking.dto.TokenDto;
import com.komsije.booking.exceptions.InvalidConfirmationTokenException;
import com.komsije.booking.model.ConfirmationToken;
import com.komsije.booking.model.Role;
import com.komsije.booking.service.interfaces.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    GuestService guestService;
    @Autowired
    HostService hostService;
    @Autowired
    EmailSenderService emailSenderService;
    @Autowired
    ConfirmationTokenService confirmationTokenService;
    @Autowired
    AccountService accountService;

    public Long register(RegistrationDto registrationDto) {
            if (registrationDto.getRole() == Role.Guest) {
              return   guestService.singUpUser(registrationDto);
            } else if (registrationDto.getRole() == Role.Host) {
                return hostService.singUpUser(registrationDto);
            }
            return -1L;
    }

    @Override
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new InvalidConfirmationTokenException("Confirmation token is not valid!"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw  new InvalidConfirmationTokenException("Account is already confirmed!");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
           /// accountService.delete(confirmationToken.getAccount().getId());
            throw new InvalidConfirmationTokenException("Confirmation token is expired, please register again!");
        }

        confirmationTokenService.setConfirmedAt(token);
//        accountService.activateAccount(confirmationToken.getAccount().getEmail());
        return "confirmed";
    }


    private String buildEmail(String name, String link) {
        return "<!DOCTYPE html><html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head>\n" +
                "  <title> Welcome to [Coded Mails] </title>\n" +
                "  <!--[if !mso]><!-- -->\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "  <!--<![endif]-->\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" +
                "  <style type=\"text/css\">\n" +
                "    #outlook a {\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "\n" +
                "    body {\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      -webkit-text-size-adjust: 100%;\n" +
                "      -ms-text-size-adjust: 100%;\n" +
                "    }\n" +
                "\n" +
                "    table,\n" +
                "    td {\n" +
                "      border-collapse: collapse;\n" +
                "      mso-table-lspace: 0pt;\n" +
                "      mso-table-rspace: 0pt;\n" +
                "    }\n" +
                "\n" +
                "    img {\n" +
                "      border: 0;\n" +
                "      height: auto;\n" +
                "      line-height: 100%;\n" +
                "      outline: none;\n" +
                "      text-decoration: none;\n" +
                "      -ms-interpolation-mode: bicubic;\n" +
                "    }\n" +
                "\n" +
                "    p {\n" +
                "      display: block;\n" +
                "      margin: 13px 0;\n" +
                "    }\n" +
                "  </style>\n" +
                "  <!--[if mso]>\n" +
                "        <xml>\n" +
                "        <o:OfficeDocumentSettings>\n" +
                "          <o:AllowPNG/>\n" +
                "          <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                "        </o:OfficeDocumentSettings>\n" +
                "        </xml>\n" +
                "        <![endif]-->\n" +
                "  <!--[if lte mso 11]>\n" +
                "        <style type=\"text/css\">\n" +
                "          .mj-outlook-group-fix { width:100% !important; }\n" +
                "        </style>\n" +
                "        <![endif]-->\n" +
                "  <!--[if !mso]><!-->\n" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Muli:300,400,700\" rel=\"stylesheet\" type=\"text/css\" />\n" +
                "  <style type=\"text/css\">\n" +
                "    @import url(https://fonts.googleapis.com/css?family=Muli:300,400,700);\n" +
                "  </style>\n" +
                "  <!--<![endif]-->\n" +
                "  <style type=\"text/css\">\n" +
                "    @media only screen and (min-width:480px) {\n" +
                "      .mj-column-per-100 {\n" +
                "        width: 100% !important;\n" +
                "        max-width: 100%;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "  <style type=\"text/css\">\n" +
                "    @media only screen and (max-width:480px) {\n" +
                "      table.mj-full-width-mobile {\n" +
                "        width: 100% !important;\n" +
                "      }\n" +
                "\n" +
                "      td.mj-full-width-mobile {\n" +
                "        width: auto !important;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "  <style type=\"text/css\">\n" +
                "    a,\n" +
                "    span,\n" +
                "    td,\n" +
                "    th {\n" +
                "      -webkit-font-smoothing: antialiased !important;\n" +
                "      -moz-osx-font-smoothing: grayscale !important;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"background-color:#f9f0e6;\">\n" +
                "  <div style=\"display:none;font-size:1px;color:#ffffff;line-height:1px;max-height:0px;max-width:0px;opacity:0;overflow:hidden;\"> Preview - Welcome to Coded Mails </div>\n" +
                "  <div style=\"background-color:#f9f0e6;\">\n" +
                "    <!--[if mso | IE]>\n" +
                "      <table\n" +
                "         align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\"\n" +
                "      >\n" +
                "        <tr>\n" +
                "          <td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\">\n" +
                "      <![endif]-->\n" +
                "    <div style=\"margin:0px auto;max-width:600px;\">\n" +
                "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\">\n" +
                "        <tbody>\n" +
                "          <tr>\n" +
                "            <td style=\"direction:ltr;font-size:0px;padding:20px 0;text-align:center;\">\n" +
                "              <!--[if mso | IE]>\n" +
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                \n" +
                "        <tr>\n" +
                "      \n" +
                "            <td\n" +
                "               class=\"\" style=\"vertical-align:top;width:600px;\"\n" +
                "            >\n" +
                "          <![endif]-->\n" +
                "              <div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"font-size:0px;word-break:break-word;\">\n" +
                "                      <!--[if mso | IE]>\n" +
                "    \n" +
                "        <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td height=\"20\" style=\"vertical-align:top;height:20px;\">\n" +
                "      \n" +
                "    <![endif]-->\n" +
                "                      <div style=\"height:20px;\">   </div>\n" +
                "                      <!--[if mso | IE]>\n" +
                "    \n" +
                "        </td></tr></table>\n" +
                "      \n" +
                "    <![endif]-->\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" +
                "                      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px;\">\n" +
                "                        <tbody>\n" +
                "                          <tr>\n" +
                "                            <td style=\"width:75px;\">\n" +
                "                              <img height=\"auto\" src=\"https://codedmails.com/images/logo-circle-gray.png\" style=\"border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;font-size:13px;\" width=\"75\" />\n" +
                "                            </td>\n" +
                "                          </tr>\n" +
                "                        </tbody>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </div>\n" +
                "              <!--[if mso | IE]>\n" +
                "            </td>\n" +
                "          \n" +
                "        </tr>\n" +
                "      \n" +
                "                  </table>\n" +
                "                <![endif]-->\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody>\n" +
                "      </table>\n" +
                "    </div>\n" +
                "    <!--[if mso | IE]>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      \n" +
                "        <table\n" +
                "           align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:600px;\" width=\"600\"\n" +
                "        >\n" +
                "          <tr>\n" +
                "            <td  style=\"line-height:0;font-size:0;mso-line-height-rule:exactly;\">\n" +
                "              <v:image\n" +
                "                 style=\"border:0;mso-position-horizontal:center;position:absolute;top:0;width:600px;z-index:-3;\" src=\"https://images.unsplash.com/photo-1536560035542-1326fab3a507?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=80\" xmlns:v=\"urn:schemas-microsoft-com:vml\"\n" +
                "              />\n" +
                "      <![endif]-->\n" +
                "    <div style=\"margin:0 auto;max-width:600px;\">\n" +
                "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\">\n" +
                "        <tbody><tr style=\"vertical-align:top;\">\n" +
                "          <td background=\"https://images.unsplash.com/photo-1536560035542-1326fab3a507?ixlib=rb-1.2.1&amp;ixid=eyJhcHBfaWQiOjEyMDd9&amp;auto=format&amp;fit=crop&amp;w=800&amp;q=80\" style=\"background:#2a3448 url(https://images.unsplash.com/photo-1536560035542-1326fab3a507?ixlib=rb-1.2.1&amp;ixid=eyJhcHBfaWQiOjEyMDd9&amp;auto=format&amp;fit=crop&amp;w=800&amp;q=80) no-repeat center center / cover;background-position:center center;background-repeat:no-repeat;padding:0px;vertical-align:top;\" height=\"300\">\n" +
                "            <!--[if mso | IE]>\n" +
                "        <table\n" +
                "           border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:600px;\" width=\"600\"\n" +
                "        >\n" +
                "          <tr>\n" +
                "            <td  style=\"\">\n" +
                "      <![endif]-->\n" +
                "            <div class=\"mj-hero-content\" style=\"margin:0px auto;\">\n" +
                "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;margin:0px;\">\n" +
                "                <tbody><tr>\n" +
                "                  <td style=\"\">\n" +
                "                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;margin:0px;\">\n" +
                "                    </table>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </tbody></table>\n" +
                "            </div>\n" +
                "            <!--[if mso | IE]>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      <![endif]-->\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </tbody></table>\n" +
                "    </div>\n" +
                "    <!--[if mso | IE]>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "    \n" +
                "      <table\n" +
                "         align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\"\n" +
                "      >\n" +
                "        <tr>\n" +
                "          <td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\">\n" +
                "      <![endif]-->\n" +
                "    <div style=\"background:#ffffff;background-color:#ffffff;margin:0px auto;max-width:600px;\">\n" +
                "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"background:#ffffff;background-color:#ffffff;width:100%;\">\n" +
                "        <tbody>\n" +
                "          <tr>\n" +
                "            <td style=\"direction:ltr;font-size:0px;padding:20px 0;text-align:center;\">\n" +
                "              <!--[if mso | IE]>\n" +
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                \n" +
                "        <tr>\n" +
                "      \n" +
                "            <td\n" +
                "               class=\"\" style=\"vertical-align:top;width:600px;\"\n" +
                "            >\n" +
                "          <![endif]-->\n" +
                "              <div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" +
                "                      <div style=\"font-family:Muli, Arial, sans-serif;font-size:20px;font-weight:400;line-height:30px;text-align:left;color:#333333;\">\n" +
                "                        <h1 style=\"margin: 0; font-size: 24px; line-height: normal; font-weight: bold;\">Hi " + name + ". Please activate your account!</h1>\n" +
                "                      </div>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" +
                "                      <p style=\"border-top: solid 1px #F4F5FB; font-size: 1px; margin: 0px auto; width: 100%;\">\n" +
                "                      </p>\n" +
                "                      <!--[if mso | IE]>\n" +
                "        <table\n" +
                "           align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-top:solid 1px #F4F5FB;font-size:1px;margin:0px auto;width:550px;\" role=\"presentation\" width=\"550px\"\n" +
                "        >\n" +
                "          <tr>\n" +
                "            <td style=\"height:0;line-height:0;\">\n" +
                "              &nbsp;\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "      <![endif]-->\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" +
                "                      <div style=\"font-family:Muli, Arial, sans-serif;font-size:16px;font-weight:400;line-height:20px;text-align:left;color:#333333;\">\n" +
                "                        <p style=\"margin: 0;\"> You've registered on our site and this is your activation email.</p>\n" +
                "                      </div>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" +
                "                      <div style=\"font-family:Muli, Arial, sans-serif;font-size:16px;font-weight:400;line-height:20px;text-align:left;color:#333333;\">\n" +
                "                        <p style=\"margin: 0;\"> You have 24 hours to activate your account. </p>\n" +
                "                      </div>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" vertical-align=\"middle\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" +
                "                      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse:separate;line-height:100%;\">\n" +
                "                        <tbody><tr>\n" +
                "                          <td align=\"center\" bgcolor=\"#e6b07e\" role=\"presentation\" style=\"border:2px solid #e6b07e;border-radius:3px;cursor:auto;mso-padding-alt:8px 16px;background:#e6b07e;\" valign=\"middle\">\n" +
                "                            <a href=\"" + link + "\" style=\"display: inline-block; background: #da893e; color: white; font-family: Muli, Arial, sans-serif; font-size: 13px; font-weight: normal; line-height: 30px; margin: 0; text-decoration: none; text-transform: none; padding: 8px 16px; mso-padding-alt: 0px; border-radius: 3px;\" target=\"_blank\"> Activate! </a>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </tbody></table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" +
                "                      <div style=\"font-family:Muli, Arial, sans-serif;font-size:14px;font-weight:400;line-height:20px;text-align:left;color:#333333;\">\n" +
                "                        <p style=\"margin: 0;\"></p>Don't want to activate account on booking app? You can ignore this email.<p style=\"margin: 0;\"></p>\n" +
                "                      </div>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </div>\n" +
                "              <!--[if mso | IE]>\n" +
                "            </td>\n" +
                "          \n" +
                "        </tr>\n" +
                "      \n" +
                "                  </table>\n" +
                "                <![endif]-->\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody>\n" +
                "      </table>\n" +
                "    </div>\n" +
                "    <!--[if mso | IE]>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      \n" +
                "      <table\n" +
                "         align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\"\n" +
                "      >\n" +
                "        <tr>\n" +
                "          <td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\">\n" +
                "      <![endif]-->\n" +
                "    <div style=\"margin:0px auto;max-width:600px;\">\n" +
                "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\">\n" +
                "        <tbody>\n" +
                "          <tr>\n" +
                "            <td style=\"direction:ltr;font-size:0px;padding:20px 0;text-align:center;\">\n" +
                "              <!--[if mso | IE]>\n" +
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                \n" +
                "        <tr>\n" +
                "      \n" +
                "            <td\n" +
                "               class=\"\" style=\"vertical-align:top;width:600px;\"\n" +
                "            >\n" +
                "          <![endif]-->\n" +
                "              <div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\">\n" +
                "                      <div style=\"font-family:Muli, Arial, sans-serif;font-size:14px;font-weight:400;line-height:20px;text-align:center;color:#333333;\">© 2023 [Booking app :D], Inc. 123 Louise St. #12, Novi Sad</div>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </div>\n" +
                "              <!--[if mso | IE]>\n" +
                "            </td>\n" +
                "          \n" +
                "        </tr>\n" +
                "      \n" +
                "                  </table>\n" +
                "                <![endif]-->\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody>\n" +
                "      </table>\n" +
                "    </div>\n" +
                "    <!--[if mso | IE]>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      \n" +
                "      <table\n" +
                "         align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\"\n" +
                "      >\n" +
                "        <tr>\n" +
                "          <td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\">\n" +
                "      <![endif]-->\n" +
                "    <div style=\"margin:0px auto;max-width:600px;\">\n" +
                "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\">\n" +
                "        <tbody>\n" +
                "          <tr>\n" +
                "            <td style=\"direction:ltr;font-size:0px;padding:20px 0;text-align:center;\">\n" +
                "              <!--[if mso | IE]>\n" +
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                \n" +
                "        <tr>\n" +
                "      \n" +
                "            <td\n" +
                "               class=\"\" style=\"vertical-align:top;width:600px;\"\n" +
                "            >\n" +
                "          <![endif]-->\n" +
                "              <div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\">\n" +
                "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"font-size:0px;word-break:break-word;\">\n" +
                "                      <!--[if mso | IE]>\n" +
                "    \n" +
                "        <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td height=\"1\" style=\"vertical-align:top;height:1px;\">\n" +
                "      \n" +
                "    <![endif]-->\n" +
                "                      <div style=\"height:1px;\">   </div>\n" +
                "                      <!--[if mso | IE]>\n" +
                "    \n" +
                "        </td></tr></table>\n" +
                "      \n" +
                "    <![endif]-->\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </div>\n" +
                "              <!--[if mso | IE]>\n" +
                "            </td>\n" +
                "          \n" +
                "        </tr>\n" +
                "      \n" +
                "                  </table>\n" +
                "                <![endif]-->\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody>\n" +
                "      </table>\n" +
                "    </div>\n" +
                "    <!--[if mso | IE]>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "      <![endif]-->\n" +
                "  </div>\n" +
                "\n" +
                "\n" +
                "</body></html>";
    }
}
