function [result, opt_y, iterates_y, iterates_x] = afs(A, b, c, y)
A_size = size(A);
b_size = size(b);
c_size = size(c);
% b, cの次元が合っていなければresult = -2,初期点が内点許容解でなければresult = -1を返す
if b_size(1) ~= A_size(1) || c_size(1) ~= A_size(2)
    result = -2;
elseif c-A'*y <= 0
    result = -1;
end
% iterates_y の最初の行にy'を格納
iterates_y(1,:) = y';
% アフィンスケーリング法にしたがって,s,S^-1,G=A*S^-1*A'を求める
s = c-A'*y;
S = diag(s);
S_inv = diag(ones(size(s))./s);
S_inv(S_inv == Inf) = 0;
G = A*S_inv;
G = G*G';
% iterates_xの初期値を格納する
iterates_x(1,:) = S_inv^2*A'*linsolve(G,b);
% 最適解が見つかればresult=0,最大反復回数を超えていればresult=1
for k = 1:100
    % y^(k+1) の式にしたがってyを更新する
    % 分数箇所を分母と分子で別々に計算する
    top = linsolve(G,b);
    bottom = sqrt(b'*top);
    y = y + top/bottom;
    % アフィンスケーリング法にしたがって,s,S^-1,G=A*S^-1*A'を求める
    s = c-A'*y;
    S_inv = diag(ones(size(s))./s);
    S_inv(S_inv == Inf) = 0;
    G = A*S_inv;
    G = G*G';
    % 反復点,双対推定を格納
    iterates_y(k+1,:) = y';
    iterates_x(k+1,:) = S_inv^2 * A' * linsolve(G,b);
    % 最適性の判定をする
    if bottom < 10^-4
        result = 0;
        break;
    else
        result = 1;
    end
end
if result == 0
    opt_y = iterates_y(k+1,:);
end